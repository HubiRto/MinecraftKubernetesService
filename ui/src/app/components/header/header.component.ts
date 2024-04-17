import {Component, Renderer2} from '@angular/core';
import {RouterLink} from "@angular/router";
import {SidebarService} from "../../services/sidebar.service";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  constructor(
    private sidebarService: SidebarService
  ) {}

  toggleDarkMode() {
    const body = document.querySelector('body');
    body.classList.toggle("dark");

    const modeText = document.body.querySelector(".mode-text");
    if(body.classList.contains("dark")) {
      modeText.innerHTML = "Light Mode"
    }else {
      modeText.innerHTML = "Dark Mode"
    }
  }

  toggleCloseSidebar() {
    const sidebar = document.body.querySelector('.sidebar');
    sidebar.classList.toggle("close");
    this.sidebarService.isOpen = sidebar.classList.contains("close");
  }
}
