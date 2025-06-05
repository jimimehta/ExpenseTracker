<template>
  <div>
    <h1>Expense Tracker</h1>

    <h2>Filter Expenses</h2>
    <!-- Filter Controls -->
    <div class="filters">
      <div>
        <label for="category">Category:</label>
        <select id="category" v-model="filters.category">
          <option value="" disabled>Select Category</option>
          <option value="Groceries">Groceries</option>
          <option value="Utilities">Utilities</option>
          <option value="Travel">Travel</option>
          <option value="Shopping">Shopping</option>
          <option value="Bills">Bills</option>
          <option value="Other">Other</option>
        </select>
      </div>

      <div>
        <label for="startDate">From:</label>
        <input type="date" id="startDate" v-model="filters.startDate">
      </div>

      <div>
        <label for="endDate">To:</label>
        <input type="date" id="endDate" v-model="filters.endDate">
      </div>

      <div>
        <label for="minAmount">Min Amount:</label>
        <input type="number" id="minAmount" v-model="filters.minAmount">
      </div>

      <div>
        <label for="maxAmount">Max Amount:</label>
        <input type="number" id="maxAmount" v-model="filters.maxAmount">
      </div>

      <button @click="applyFilters">Apply Filters</button>
      <button @click="resetFilters">Reset</button>
    </div>

    <expense-form @expense-added="handleExpenseAdded" />
    
    <div class="all-expenses-section">
      <h2>All Expenses</h2>
      <ul>
        <li v-for="expense in expenses" :key="expense.id">
          {{ expense.description }} - {{ expense.amount }} - {{ expense.date }} - {{ expense.category }}
          <button @click="archiveExpense(expense.id)">Archive</button>
        </li>
      </ul>
    </div>

    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 0">Previous</button>
      <span>Page {{ currentPage + 1 }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage >= totalPages - 1">Next</button>
      <span>Total items: {{ totalItems }}</span>
    </div>

    <div class="archived-section">
      <h2>Archived Expenses</h2>
      <ul>
        <li v-for="expense in archivedExpenses" :key="expense.id">
          {{ expense.description }} - {{ expense.amount }} - {{ expense.date }} - {{ expense.category }}
        </li>
      </ul>
      <div class="pagination">
        <button @click="prevArchivedPage" :disabled="archivedPage === 0">Previous</button>
        <span>Page {{ archivedPage + 1 }} of {{ archivedTotalPages }}</span>
        <button @click="nextArchivedPage" :disabled="archivedPage >= archivedTotalPages - 1">Next</button>
        <span>Total items: {{ totalArchivedItems }}</span>
      </div>
    </div>

    <button @click="logout">Logout</button>
  </div>
</template>

<script>
import axios from 'axios';
import ExpenseForm from './ExpenseForm.vue';

export default {
  components: { ExpenseForm },
  data() {
  return {
    expenses: [],
    archivedExpenses: [],
    filters: {
      category: '',
      startDate: '',
      endDate: '',
      minAmount: '',
      maxAmount: ''
    },
    currentPage: 0,
    pageSize: 5,
    totalItems: 0,
    totalPages: 0,
    archivedPage: 0,
    archivedTotalPages: 0,
    totalArchivedItems: 0
  };
},

  mounted() {
    this.fetchExpenses();
    this.fetchArchivedExpenses();
  },
  methods: {
    fetchExpenses() {
      const auth = localStorage.getItem('auth');
      if (!auth) {
        this.$router.push('/');
        return;
      }

      let url = 'http://localhost:8080/api/expenses/filter?';
      url += `page=${this.currentPage}&size=${this.pageSize}`;

      if (this.filters.category) url += `&category=${this.filters.category}`;
      if (this.filters.startDate) url += `&startDate=${this.filters.startDate}`;
      if (this.filters.endDate) url += `&endDate=${this.filters.endDate}`;
      if (this.filters.minAmount) url += `&minAmount=${this.filters.minAmount}`;
      if (this.filters.maxAmount) url += `&maxAmount=${this.filters.maxAmount}`;

      axios.get(url, {
        headers: { 'Authorization': `Basic ${auth}` }
      })
      .then(response => {
        this.expenses = response.data.expenses;
        this.currentPage = response.data.currentPage;
        this.totalItems = response.data.totalItems;
        this.totalPages = response.data.totalPages;
      })
      .catch(() => this.$router.push('/'));
    },

    fetchArchivedExpenses() {
      const auth = localStorage.getItem('auth');
      if (!auth) return;

      let url = `http://localhost:8080/api/expenses/archived?page=${this.archivedPage}&size=${this.pageSize}`;

      axios.get(url, {
        headers: { 'Authorization': `Basic ${auth}` }
      })
      .then(response => {
        this.archivedExpenses = response.data.expenses || response.data.content || [];
        this.archivedPage = response.data.currentPage ?? 0;
        this.archivedTotalPages = response.data.totalPages ?? 1;
        this.totalArchivedItems = response.data.totalItems ?? 0;
      })
      .catch(error => console.error('Failed to load archived expenses', error));
    },

    handleExpenseAdded() {
      this.fetchExpenses();
      this.fetchArchivedExpenses();
    },

    applyFilters() {
      this.currentPage = 0;
      this.fetchExpenses();
    },

    resetFilters() {
      this.filters = {
        category: '',
        startDate: '',
        endDate: '',
        minAmount: '',
        maxAmount: ''
      };
      this.currentPage = 0;
      this.fetchExpenses();
    },

    prevPage() {
      if (this.currentPage > 0) {
        this.currentPage--;
        this.fetchExpenses();
      }
    },

    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++;
        this.fetchExpenses();
      }
    },

    prevArchivedPage() {
      if (this.archivedPage > 0) {
        this.archivedPage--;
        this.fetchArchivedExpenses();
      }
    },

    nextArchivedPage() {
      if (this.archivedPage < this.archivedTotalPages - 1) {
        this.archivedPage++;
        this.fetchArchivedExpenses();
      }
    },

    archiveExpense(id) {
      const auth = localStorage.getItem('auth');
      axios.delete(`http://localhost:8080/api/expenses/${id}`, {
        headers: { 'Authorization': `Basic ${auth}` }
      })
      .then(() => {
        this.fetchExpenses();
        this.fetchArchivedExpenses();
      })
      .catch(error => console.error(error));
    },

    logout() {
      localStorage.removeItem('auth');
      this.$router.push('/');
    }
  }
};
</script>
