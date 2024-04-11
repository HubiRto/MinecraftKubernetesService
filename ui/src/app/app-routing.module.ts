import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ServersListComponent} from "./components/servers-list/servers-list.component";
import {ServerPanelComponent} from "./components/server-panel/server-panel.component";

const routes: Routes = [
  {path: 'servers', component: ServersListComponent},
  {path: 'servers/info/:uuid', component: ServerPanelComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
