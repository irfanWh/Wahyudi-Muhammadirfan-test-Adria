
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Account {
  id: string;
  customerReference: string;
  status: 'ACTIVE' | 'BLOCKED';
  currency: string;
  balance: number;
}

interface BankTransaction {
  id: string;
  accountId: string;
  amount: number;
  currency: string;
  merchantCategory: string;
  country: string;
  createdAt: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  private http = inject(HttpClient);

  accounts = signal<Account[]>([]);
  transactions = signal<BankTransaction[]>([]);
  selectedView = signal<'accounts' | 'transactions'>('accounts');
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

  showAccounts() {
    this.selectedView.set('accounts');
  }

  showTransactions() {
    this.selectedView.set('transactions');
    this.loadTransactions();
  }

  refresh() {
    this.selectedView() === 'accounts'
      ? this.loadAccounts()
      : this.loadTransactions();
  }

  loadTransactions() {
    this.loading.set(true);
    this.error.set('');

    this.http.get<BankTransaction[]>('/transactions').subscribe({
      next: data => {
        this.transactions.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les transactions.');
        this.loading.set(false);
      }
    });
  }

  shortId(id: string) {
    return id.substring(0, 8);
  }
}
