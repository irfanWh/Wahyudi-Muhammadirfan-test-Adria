
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

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
  imports: [FormsModule],
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
  creating = signal(false);
  formError = signal('');
  success = signal('');

  transactionForm = {
    accountId: '',
    amount: null as number | null,
    currency: '',
    merchantCategory: 'SHOPPING',
    country: 'MA'
  };

  activeCount = computed(() =>
    this.accounts().filter(a => a.status === 'ACTIVE').length
  );

  blockedCount = computed(() =>
    this.accounts().filter(a => a.status === 'BLOCKED').length
  );

  activeAccounts = computed(() =>
    this.accounts().filter(a => a.status === 'ACTIVE')
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

  onAccountChange() {
    const account = this.accounts().find(
      item => item.id === this.transactionForm.accountId
    );
    this.transactionForm.currency = account?.currency ?? '';
  }

  createTransaction() {
    this.formError.set('');
    this.success.set('');

    if (!this.transactionForm.accountId
      || !this.transactionForm.amount
      || this.transactionForm.amount <= 0
      || this.transactionForm.amount > 1000) {
      this.formError.set('Choisissez un compte et un montant entre 0 et 1000.');
      return;
    }

    this.creating.set(true);
    this.http.post<BankTransaction>('/transactions', this.transactionForm).subscribe({
      next: () => {
        this.creating.set(false);
        this.success.set('Transaction enregistrée avec succès.');
        this.transactionForm.amount = null;
        this.loadTransactions();
        this.loadAccountsInBackground();
      },
      error: () => {
        this.creating.set(false);
        this.formError.set('Transaction refusée. Vérifiez le compte et le solde.');
      }
    });
  }

  private loadAccountsInBackground() {
    this.http.get<Account[]>('/accounts').subscribe({
      next: data => this.accounts.set(data)
    });
  }
}
