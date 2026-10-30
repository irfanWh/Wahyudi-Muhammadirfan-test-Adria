
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Account {
  id: string;
  customerReference: string;
  status: 'ACTIVE' | 'BLOCKED';
  currency: string;
  balance: number;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  private http = inject(HttpClient);

  accounts = signal<Account[]>([]);
  loading = signal(true);
  error = signal('');

  activeCount = computed(() =>
    this.accounts().filter(a => a.status === 'ACTIVE').length
  );

  blockedCount = computed(() =>
    this.accounts().filter(a => a.status === 'BLOCKED').length
  );

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.loading.set(true);
    this.error.set('');

    this.http.get<Account[]>('/accounts').subscribe({
      next: data => {
        this.accounts.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les comptes.');
        this.loading.set(false);
      }
    });
  }
}
