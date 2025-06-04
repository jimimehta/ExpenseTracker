<template>
  <div>
    <h2>Expenses</h2>

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

    <expense-form @expense-added="fetchExpenses" />

    <ul>
      <li v-for="expense in expenses" :key="expense.id">
        {{ expense.description }} - {{ expense.amount }} - {{ expense.date }} - {{ expense.category }}
        <button @click="deleteExpense(expense.id)">Delete</button>
      </li>
    </ul>

    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 0">Previous</button>
      <span>Page {{ currentPage + 1 }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage >= totalPages - 1">Next</button>
      <span>Total items: {{ totalItems }}</span>
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
      filters: {
        category: '',
        startDate: '',
        endDate: '',
        minAmount: '',
        maxAmount: ''
      },
      currentPage: 0,
      pageSize: 25,
      totalItems: 0,
      totalPages: 0
    };
  },
  mounted() {
    this.fetchExpenses();
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

    deleteExpense(id) {
      const auth = localStorage.getItem('auth');
      axios.delete(`http://localhost:8080/api/expenses/${id}`, {
        headers: { 'Authorization': `Basic ${auth}` }
      })
      .then(() => this.fetchExpenses())
      .catch(error => console.error(error));
    },

    logout() {
      localStorage.removeItem('auth');
      this.$router.push('/');
    }
  }
};
</script>

