import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

interface Language {
  code: string;
  name: string;
  flag: string;
}

@Component({
  selector: 'app-language-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './language-selector.component.html',
  styleUrl: './language-selector.component.scss'
})
export class LanguageSelectorComponent {
  private translate = inject(TranslateService);
  
  languages: Language[] = [
    { code: 'pt-BR', name: 'Português', flag: '🇧🇷' },
    { code: 'en-US', name: 'English', flag: '🇺🇸' },
    { code: 'es-ES', name: 'Español', flag: '🇪🇸' }
  ];

  currentLanguage: Language;
  isOpen = false;

  constructor() {
    const currentLang = this.translate.currentLang || this.translate.defaultLang || 'pt-BR';
    this.currentLanguage = this.languages.find(l => l.code === currentLang) || this.languages[0];
  }

  toggleDropdown(): void {
    this.isOpen = !this.isOpen;
  }

  selectLanguage(language: Language): void {
    this.currentLanguage = language;
    this.translate.use(language.code);
    this.isOpen = false;
    localStorage.setItem('preferredLanguage', language.code);
  }

  closeDropdown(): void {
    this.isOpen = false;
  }
}
