import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ServersListComponent} from "./components/servers-list/servers-list.component";
import {ServerPanelComponent} from "./components/server-panel/server-panel.component";
import {LoginComponent} from "./components/login/login.component";
import {RegisterComponent} from "./components/register/register.component";

const routes: Routes = [
  {path: 'servers', component: ServersListComponent},
  {path: 'servers/info/:uuid', component: ServerPanelComponent},
  {path: 'auth/login', component: LoginComponent},
  {path: 'auth/register', component: RegisterComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
