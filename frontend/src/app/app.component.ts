import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: '<router-outlet></router-outlet>',
  styles: []
})
export class AppComponent {
  private translate = inject(TranslateService);
  title = 'GED - Gestão Eletrônica de Documentos';

  constructor() {
    // Configurar idiomas disponíveis
    this.translate.addLangs(['pt-BR', 'en-US', 'es-ES']);
    this.translate.setDefaultLang('pt-BR');
    
    // Usar idioma salvo ou padrão
    const savedLang = localStorage.getItem('preferredLanguage') || 'pt-BR';
    this.translate.use(savedLang);
  }
}
