import {Component, OnInit} from '@angular/core';
import {AsyncPipe, CommonModule, JsonPipe, NgForOf} from "@angular/common";
import {map, Observable, of, startWith} from "rxjs";
import {AppState} from "../../interfaces/app-state";
import {CustomResponse} from "../../interfaces/custom-response";
import {DataState} from "../../enums/data-state";
import {catchError} from "rxjs/operators";
import {ServerServiceService} from "../../services/server-service.service";
import {Router, RouterLink} from "@angular/router";
import {SidebarService} from "../../services/sidebar.service";

@Component({
  selector: 'app-servers-list',
  standalone: true,
  imports: [
    NgForOf,
    AsyncPipe,
    JsonPipe,
    CommonModule,
    RouterLink
  ],
  templateUrl: './servers-list.component.html',
  styleUrl: './servers-list.component.css'
})
export class ServersListComponent implements OnInit {
  appState$: Observable<AppState<CustomResponse>> | undefined;
  readonly DateState = DataState;

  constructor(
    protected serverService: ServerServiceService,
    protected sidebarService: SidebarService,
  ) {}

  ngOnInit(): void {
    this.loadServers();
  }

  loadServers(): void {
    this.appState$ = this.serverService.servers$.pipe(
      map(response => ({
        dataState: DataState.LOADED_STATE,
        appData: response
      })),
      startWith({ dataState: DataState.LOADING_STATE }),
      catchError((error: string) =>
        of({ dataState: DataState.ERROR_STATE, error })
      )
    );
  }


  delete(serverId: number): void {
    this.serverService.delete$(serverId).subscribe(() => {
      this.loadServers();
    })
  }

  protected readonly Number = Number;
}
