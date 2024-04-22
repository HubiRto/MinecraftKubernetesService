import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {CustomResponse} from "../../interfaces/custom-response";
import {AsyncPipe, NgClass, NgForOf, NgIf, NgSwitch, NgSwitchCase} from "@angular/common";
import {HttpClient} from "@angular/common/http";
import {CompatClient, Stomp} from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs.js"
import {InfiniteScrollModule} from "ngx-infinite-scroll";
import {SidebarService} from "../../services/sidebar.service";
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";
import {interval} from "rxjs";
import {LineChartModule} from "@swimlane/ngx-charts";
import {Chart, ChartModule} from "angular-highcharts";
import {ServerUsage} from "../../interfaces/server-usage";
import {HeaderComponent} from "../header/header.component";

interface LoginResponse {
  token: string;
}

@Component({
  selector: 'app-server-panel',
  standalone: true,
  imports: [
    AsyncPipe,
    NgForOf,
    NgIf,
    NgSwitchCase,
    NgSwitch,
    InfiniteScrollModule,
    NgClass,
    LineChartModule,
    ChartModule,
    HeaderComponent
  ],
  templateUrl: './server-panel.component.html',
  styleUrl: './server-panel.component.css'
})
export class ServerPanelComponent implements OnInit, OnDestroy {
  private baseUrl = 'http://127.0.0.1:8080/api/v1/server';
  private stompClient: CompatClient;
  private isLatWarn: boolean = false;
  uuid: string;

  ramUsageChart = new Chart({
    chart: {
      type: 'areaspline'
    },
    title: {
      text: 'RAM Usage'
    },
    credits: {
      enabled: false
    },
    plotOptions: {
      areaspline: {
        fillColor: {
          linearGradient: { x1: 0, x2: 0, y1: 0, y2: 1 },
          stops: [
            [0, '#67c4f4'],
            [1, '#bae1ff']
          ]
        },
      },
    },
    xAxis: {
      type: 'datetime',
      tickPixelInterval: 150,
      maxPadding: 0.1
    },
    yAxis: {
      title: {
        text: 'MB'
      },
      plotLines: [
        {
          value: 0,
          width: 1,
          color: '#1b85b8'
        }
      ]
    },
    tooltip: {
      headerFormat: '<b>{series.name}</b><br/>',
      pointFormat: '{point.x:%Y-%m-%d %H:%M:%S}<br/>{point.y:.2f}'
    },
    series: [
      {
        name: 'RAM',
        data: [],
        maxPointWidth: 10
      } as any
    ]
  });

  cpuUsageChart = new Chart({
    chart: {
      type: 'areaspline',
    },
    title: {
      text: 'CPU Usage'
    },
    credits: {
      enabled: false
    },
    xAxis: {
      type: 'datetime',
      tickPixelInterval: 150,
      maxPadding: 0.1
    },
    plotOptions: {
      areaspline: {
        color: '#289c54',
        fillColor: {
          linearGradient: { x1: 0, x2: 0, y1: 0, y2: 1 },
          stops: [
            [0, '#63c688'],
            [1, '#c4eed4']
          ]
        },
      },
    },
    yAxis: {
      title: {
        text: 'CPU (cores)'
      },
      plotLines: [
        {
          value: 0,
          width: 1,
          color: '#baffc9'
        }
      ]
    },
    tooltip: {
      headerFormat: '<b>{series.name}</b><br/>',
      pointFormat: '{point.x:%Y-%m-%d %H:%M:%S}<br/>{point.y:.2f}'
    },
    series: [
      {
        name: 'CPU (cores)',
        data: [],
        maxPointWidth: 10
      } as any
    ]
  });

  addPointToChart(chart: Chart, val: number, dateString: string) {
    const date = new Date(dateString);
    const formattedDate = date.getTime();
    chart.addPoint([formattedDate, val]);
  }



  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    protected sidebarService: SidebarService,
    private sanitizer: DomSanitizer
  ) {
  }

  logs: SafeHtml[] = [];

  addLog(log: string) {
    this.http.post<CustomResponse>(`${this.baseUrl}/command/exec/${this.uuid}`, log)
      .subscribe(response => {
        this.logs.push(this.convertTextToSpan(response.data.cmdResult));
        this.scrollToBottom();
      });
  }

  removePrefixFromLogs(logs: string[]): string[] {
    const regex = /\[([^[\]]+)]/g;

    for (let i = 0; i < logs.length; i++) {
      logs[i] = logs[i].replace(regex, (match, p1, offset) => {
        if (offset === 0) {
          return match;
        } else {
          return `[${p1.slice(-4)}]`;
        }
      });
    }
    return logs;
  }

  convertTextToSpan(text: string): SafeHtml {
    const colorMap: { [key: string]: string } = {
      '0': '#D8BFD8', // Pastelowy fiolet
      '1': '#ADD8E6', // Pastelowy niebieski
      '2': '#90EE90', // Pastelowy zielony
      '3': '#87CEEB', // Pastelowy błękit
      '4': '#FFA07A', // Pastelowy łososiowy
      '5': '#FFB6C1', // Pastelowy różowy
      '6': '#FFD700', // Pastelowy złoty
      '7': '#D3D3D3', // Pastelowy szary
      '8': '#C0C0C0', // Srebrny
      '9': '#B0E0E6', // Jasnoniebieski
      'a': '#98FB98', // Jasnozielony
      'b': '#AFEEEE', // Błękitny
      'c': '#FFA07A', // Pastelowy łososiowy
      'd': '#DB7093', // Pastelowy różowy
      'e': '#FFD700', // Pastelowy złoty
      'f': '#FFFFFF', // Biały
      'r': '#FFFFFF', // Reset kolorów
    };

    let spanText = '> ';
    let currentColor = '#FFFFFF';
    for (let i = 0; i < text.length; i++) {
      if (text[i] === '§' && i + 1 < text.length) {
        const colorCode = text[i + 1];
        if (colorCode in colorMap) {
          currentColor = colorMap[colorCode];
        }
        i++;
      } else {
        spanText += `<span style="color: ${currentColor};">${text[i]}</span>`;
      }
    }
    return this.sanitizer.bypassSecurityTrustHtml(spanText);
  }

  colorizeLog(log: string): SafeHtml {
    const regexSquare = /\[([^[\]]+)]\s*\[([^[\]]+)]\s*:\s*(.*)/g;
    const regexCircle = /\((.*?)\)/g;

    if (regexSquare.test(log)) {
      log = log.replace(regexCircle, (_match, group) => {
        return `(<span style="color: #ff7bd8">${group}</span>)`;
      });

      log = log.replace(regexSquare, (_match, p1, p2, p3) => {
        switch (p2) {
          case 'WARN':
            this.isLatWarn = true;
            return `[<span style="color: #baffc9">${p1}</span>] [<span style="color: #ffdfba;">${p2}</span>]: <span style="color: #ffdfba">${p3}</span>`;
          case 'INFO':
            if (this.isLatWarn) this.isLatWarn = false;
            return `[<span style="color: #baffc9">${p1}</span>] [<span style="color: #bae1ff;">${p2}</span>]: ${p3}`;
          default:
            return `[<span style="color: #baffc9">${p1}</span>] [${p2}]: ${p3}`;
        }
      });
    } else {
      if (this.isLatWarn) {
        log = '<span style="color: #ffdfba;">' + log + '</span>'
      }
    }
    return this.sanitizer.bypassSecurityTrustHtml(log);
  }

  ngOnInit(): void {
    this.uuid = this.route.snapshot.paramMap.get('uuid');

    let token: string = '';
    this.http.post<LoginResponse>('http://localhost:8080/api/v1/auth/login', {
      email: "hubert.rybicki.hr1@gmail.com",
      password: "1234"
    }).subscribe(res => {
        token = res.token
      console.log(token)
    });

    this.http.get<string[]>(`${this.baseUrl}/logs/${this.uuid}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .subscribe(response => {
        let responseLogs = this.removePrefixFromLogs(response);
        for (let i = 0; i < responseLogs.length; i++) {
          this.logs.push(this.colorizeLog(responseLogs[i]));
        }
        this.scrollToBottom();
        this.connect(this.uuid);
      });

    const interval$ = interval(5000);
    interval$.subscribe(() => {
      this.http.get<ServerUsage>(`${this.baseUrl}/usage/${this.uuid}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
        }
      })
        .subscribe(response => {
          const ramUsage = Math.floor(Number(response.ramUsage) / 1024);
          this.addPointToChart(this.ramUsageChart, ramUsage, response.timeStamp.toString())
          this.addPointToChart(this.cpuUsageChart, Math.floor(response.cpuUsage/1000000), response.timeStamp.toString())
        });
    });

  }

  ngOnDestroy(): void {
    this.closeWebSocket();
  }

  protected readonly Number = Number;


  connect(id: string): void {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect({}, () => {
      console.log('Connected to WebSocket');
      this.stompClient.subscribe(`/user/${id}/server/logs`, (message) => {
        console.log('Received message from WebSocket:', message.body);
        const logsArray: string[] = JSON.parse(message.body);
        for (let i = 0; i < logsArray.length; i++) {
          this.logs.push(this.colorizeLog(logsArray[i]));
        }
        this.scrollToBottom();
      });
    });
  }

  closeWebSocket(): void {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected from WebSocket');
      });
    }
  }

  scrollToBottom() {
    setTimeout(() => {
      const content = document.querySelector('.content');
      if (content) {
        content.scrollTop = content.scrollHeight;
      }
    });
  }
}
