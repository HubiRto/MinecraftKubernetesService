import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {CommonModule} from "@angular/common";
import {HeaderComponent} from "./components/header/header.component";
import {StompServiceService} from "./services/websocket/stomp-service.service";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {HighchartsChartModule} from "highcharts-angular";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    HeaderComponent,
    HighchartsChartModule
  ],
  providers: [StompServiceService, provideAnimationsAsync()],
  bootstrap: [AppComponent]
})
export class AppModule {
}
