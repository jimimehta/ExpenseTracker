<template>
  <div class="expense-container">
    <div class="header">
      <div class="header-left">
        <div class="logo-circle">
          <span class="logo-text">💸</span>
        </div>
        <h1>Expense Tracker</h1>
      </div>
      <button @click="logout" class="logout-button">Logout</button>
    </div>

    <div class="card filter-card">
      <div class="filter-header">
        <h2>Filter Active Expenses</h2>
      </div>
      <!-- Filter Controls -->
      <div class="compact-filters-container">
        <div class="compact-filters">
          <div class="compact-filter-item">
            <label for="category" class="compact-label">Category</label>
            <select id="category" v-model="filters.category" class="compact-input">
              <option value="" disabled>Select</option>
              <option value="Groceries">Groceries</option>
              <option value="Utilities">Utilities</option>
              <option value="Travel">Travel</option>
              <option value="Shopping">Shopping</option>
              <option value="Bills">Bills</option>
              <option value="Other">Other</option>
            </select>
          </div>

          <div class="compact-filter-item">
            <label for="startDate" class="compact-label">From</label>
            <input type="date" id="startDate" v-model="filters.startDate" class="compact-input">
          </div>

          <div class="compact-filter-item">
            <label for="endDate" class="compact-label">To</label>
            <input type="date" id="endDate" v-model="filters.endDate" class="compact-input">
          </div>

          <div class="compact-filter-item">
            <label for="minAmount" class="compact-label">Min $</label>
            <input type="number" id="minAmount" v-model="filters.minAmount" class="compact-input">
          </div>

          <div class="compact-filter-item">
            <label for="maxAmount" class="compact-label">Max $</label>
            <input type="number" id="maxAmount" v-model="filters.maxAmount" class="compact-input">
          </div>
          
          <div class="compact-filter-actions">
            <button @click="applyFilters" class="compact-button primary-button">
              <svg xmlns="http://www.w3.org/2000/svg" class="compact-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M3 3a1 1 0 011-1h12a1 1 0 011 1v3a1 1 0 01-.293.707L12 11.414V15a1 1 0 01-.293.707l-2 2A1 1 0 018 17v-5.586L3.293 6.707A1 1 0 013 6V3z" clip-rule="evenodd" />
              </svg>
              Apply
            </button>
            <button @click="resetFilters" class="compact-button secondary-button">
              <svg xmlns="http://www.w3.org/2000/svg" class="compact-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd" />
              </svg>
              Reset
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="add-expense-button-container">
      <button @click="showExpenseForm = true" class="add-expense-button">
        <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
        </svg>
        Add New Expense
      </button>
    </div>
    
    <expense-form 
      :is-visible="showExpenseForm" 
      @close="showExpenseForm = false" 
      @expense-added="handleExpenseAdded" 
    />
    
    <div class="card">
      <div class="tab-navigation">
        <button 
          @click="activeTab = 'all'" 
          :class="['tab-button', { active: activeTab === 'all' }]"
        >
          <h3>Active Expenses</h3>
        </button>
        <button 
          @click="activeTab = 'archived'" 
          :class="['tab-button', { active: activeTab === 'archived' }]"
        >
          <h3>Active Expenses</h3>
        </button>
      </div>
      
      <!-- Active Expenses Tab -->
      <div v-if="activeTab === 'all'" class="tab-content">
        <div class="page-size-control">
          <div class="page-size-selector">
            <label for="pageSize" class="compact-label">Items per page:</label>
            <select id="pageSize" v-model="pageSize" class="page-size-select" @change="handlePageSizeChange">
              <option v-for="size in pageSizeOptions" :key="size" :value="size">{{ size }}</option>
            </select>
          </div>
        </div>
        <div class="expense-table-container">
          <div class="expense-table-header">
            <div class="header-description">Description</div>
            <div class="header-category">Category</div>
            <div class="header-date">Date</div>
            <div class="header-amount">Amount</div>
            <div class="header-actions">Actions</div>
          </div>
          
          <div class="expense-table-body" v-if="expenses.length > 0">
            <div v-for="expense in expenses" :key="expense.id" class="expense-row">
              <div class="cell-description" :title="expense.description">{{ expense.description }}</div>
              <div class="cell-category">
                <span class="category-badge">{{ expense.category }}</span>
              </div>
              <div class="cell-date">{{ expense.date }}</div>
              <div class="cell-amount">${{ expense.amount }}</div>
              <div class="cell-actions">
                <button @click="archiveExpense(expense.id)" class="archive-button">
                  <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M4 3a2 2 0 100 4h12a2 2 0 100-4H4z" />
                    <path fill-rule="evenodd" d="M3 8h14v7a2 2 0 01-2 2H5a2 2 0 01-2-2V8zm5 3a1 1 0 011-1h2a1 1 0 110 2H9a1 1 0 01-1-1z" clip-rule="evenodd" />
                  </svg>
                  Archive
                </button>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">No expenses found</div>
        </div>

        <div class="pagination" v-if="totalPages > 0">
          <div class="pagination-center">
            <button @click="prevPage" :disabled="currentPage === 0" class="page-button">
              <span>&larr;</span> Previous
            </button>
            <span class="page-info">Page {{ currentPage + 1 }} of {{ totalPages }}</span>
            <button @click="nextPage" :disabled="currentPage >= totalPages - 1" class="page-button">
              Next <span>&rarr;</span>
            </button>
          </div>
          <span class="total-info">Total items: {{ totalItems }}</span>
        </div>
      </div>
      
      <!-- Archived Expenses Tab -->
      <div v-if="activeTab === 'archived'" class="tab-content">
        <div class="page-size-control">
          <div class="page-size-selector">
            <label for="archivedPageSize" class="compact-label">Items per page:</label>
            <select id="archivedPageSize" v-model="archivedPageSize" class="page-size-select" @change="handleArchivedPageSizeChange">
              <option v-for="size in pageSizeOptions" :key="size" :value="size">{{ size }}</option>
            </select>
          </div>
        </div>
        <div class="expense-table-container">
          <div class="expense-table-header">
            <div class="header-description">Description</div>
            <div class="header-category">Category</div>
            <div class="header-date">Date</div>
            <div class="header-amount">Amount</div>
          </div>
          
          <div class="expense-table-body" v-if="archivedExpenses.length > 0">
            <div v-for="expense in archivedExpenses" :key="expense.id" class="expense-row archived">
              <div class="cell-description" :title="expense.description">{{ expense.description }}</div>
              <div class="cell-category">
                <span class="category-badge">{{ expense.category }}</span>
              </div>
              <div class="cell-date">{{ expense.date }}</div>
              <div class="cell-amount">${{ expense.amount }}</div>
            </div>
          </div>
          <div v-else class="empty-state">No archived expenses</div>
        </div>
        
        <div class="pagination" v-if="archivedTotalPages > 0">
          <div class="pagination-center">
            <button @click="prevArchivedPage" :disabled="archivedPage === 0" class="page-button">
              <span>&larr;</span> Previous
            </button>
            <span class="page-info">Page {{ archivedPage + 1 }} of {{ archivedTotalPages }}</span>
            <button @click="nextArchivedPage" :disabled="archivedPage >= archivedTotalPages - 1" class="page-button">
              Next <span>&rarr;</span>
            </button>
          </div>
          <span class="total-info">Total items: {{ totalArchivedItems }}</span>
        </div>
      </div>
    </div>
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
    activeTab: 'all',
    showExpenseForm: false,
    filters: {
      category: '',
      startDate: '',
      endDate: '',
      minAmount: '',
      maxAmount: ''
    },
    currentPage: 0,
    pageSize: 5,
    pageSizeOptions: [5, 10, 15, 20, 25],
    totalItems: 0,
    totalPages: 0,
    archivedPage: 0,
    archivedPageSize: 5,
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

      let url = `http://localhost:8080/api/expenses/archived?page=${this.archivedPage}&size=${this.archivedPageSize}`;

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
    
    handlePageSizeChange() {
      this.currentPage = 0;
      this.fetchExpenses();
    },
    
    handleArchivedPageSizeChange() {
      this.archivedPage = 0;
      this.fetchArchivedExpenses();
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

<style scoped>
/* Base styles */
.expense-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  padding: 2rem;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  color: #cbd5e1;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo-circle {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
}

.logo-text {
  font-size: 1.5rem;
  color: #fff;
}

h1 {
  color: #fff;
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin: 0;
}

h2 {
  color: #fff;
  font-size: 1.5rem;
  font-weight: 600;
  margin-top: 0;
  margin-bottom: 1.5rem;
  letter-spacing: 0.5px;
}

.card {
  background: #1e293b;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25), 0 4px 8px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

/* Filters section */
.filter-card {
  padding: 1rem;
}

.filter-header {
  margin-bottom: 0.75rem;
}

.filter-header h2 {
  color: ;
  border: none;
  padding: 0 1.5rem;
  font-size: default;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.compact-filters-container {
  background: rgba(15, 23, 42, 0.5);
  border-radius: 8px;
  padding: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.compact-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: flex-end;
}

.compact-filter-item {
  flex: 0 0 auto;
  min-width: 120px;
  transition: all 0.2s ease;
}

.compact-label {
  font-size: 0.8rem;
  color: #94a3b8;
  margin-bottom: 0.3rem;
  display: block;
  font-weight: 500;
}

.compact-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  font-size: 0.9rem;
  background: rgba(15, 23, 42, 0.7);
  color: #fff;
  transition: border-color 0.3s, box-shadow 0.3s;
  height: 36px;
}

.compact-input:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
}

.compact-filter-actions {
  display: flex;
  gap: 0.5rem;
  margin: 0 auto;
}

.compact-button {
  padding: 0.5rem 0.75rem;
  height: 36px;
  font-size: 0.85rem;
  min-width: 0;
  white-space: nowrap;
}

.compact-icon {
  width: 1rem;
  height: 1rem;
}

@media (max-width: 768px) {
  .compact-filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .compact-filter-actions {
    margin: 0.75rem auto 0;
    justify-content: center;
    width: 100%;
  }
}

input[type="text"],
input[type="number"],
input[type="date"],
.select-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  font-size: 1rem;
  background: #0f172a;
  color: #fff;
  transition: border-color 0.3s, box-shadow 0.3s;
  box-sizing: border-box;
  max-width: 100%;
}

input[type="text"]:focus,
input[type="number"]:focus,
input[type="date"]:focus,
.select-input:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
}

input[type="text"]::placeholder,
input[type="number"]::placeholder,
.select-input::placeholder {
  color: #64748b;
}

/* Buttons */
.primary-button,
.secondary-button,
.page-button,
.archive-button,
.logout-button {
  padding: 0.75rem 1.25rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  font-size: 0.95rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
  gap: 0.5rem;
}

.icon {
  width: 1.25rem;
  height: 1.25rem;
}

.primary-button {
  background: linear-gradient(90deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
  min-width: 140px;
}

.primary-button:hover:not(:disabled) {
  background: linear-gradient(90deg, #4338ca 0%, #6d28d9 100%);
  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.3);
  transform: translateY(-1px);
}

.secondary-button {
  background: rgba(255, 255, 255, 0.1);
  color: #cbd5e1;
  border: 1px solid rgba(255, 255, 255, 0.2);
  min-width: 120px;
}

.secondary-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-1px);
}

.logout-button {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.logout-button:hover {
  background: rgba(239, 68, 68, 0.2);
}

.archive-button {
  background: rgba(255, 255, 255, 0.1);
  color: #cbd5e1;
  padding: 0.5rem 0.75rem;
  font-size: 0.85rem;
  min-width: 100px;
}

.archive-button:hover {
  background: rgba(255, 255, 255, 0.15);
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

/* Expense table */
.expense-table-container {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 1.5rem;
}

.expense-table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  background: rgba(15, 23, 42, 0.9);
  padding: 1rem;
  font-weight: 600;
  color: #94a3b8;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  font-size: 0.9rem;
  letter-spacing: 0.5px;
}

.expense-table-body {
  background: rgba(15, 23, 42, 0.5);
}

.expense-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  padding: 1rem;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.2s ease;
}

.expense-row:last-child {
  border-bottom: none;
}

.expense-row:hover {
  background: rgba(15, 23, 42, 0.8);
}

.expense-row.archived {
  opacity: 0.7;
}

.cell-description {
  font-weight: 500;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 1rem;
}

.cell-category {
  padding-right: 0.5rem;
}

.category-badge {
  color: #94a3b8;
  white-space: nowrap;
  background: rgba(148, 163, 184, 0.1);
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.85rem;
  display: inline-block;
}

.cell-date {
  color: #94a3b8;
  font-size: 0.9rem;
}

.cell-amount {
  color: #4ade80;
  font-weight: 600;
  white-space: nowrap;
  background: rgba(74, 222, 128, 0.1);
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  border: 1px solid rgba(74, 222, 128, 0.2);
  display: inline-block;
}

.cell-actions {
  text-align: right;
}

.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  color: #64748b;
  font-style: italic;
  background: rgba(15, 23, 42, 0.5);
  border-radius: 8px;
  border: 1px dashed rgba(255, 255, 255, 0.1);
}

/* Pagination */
.pagination {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
  margin-top: 1.5rem;
  padding: 1rem;
  border-radius: 8px;
  background: rgba(15, 23, 42, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.05);
  position: relative;
  justify-content: center;
}

.pagination-center {
  display: flex;
  align-items: center;
  gap: 1rem;
  justify-content: center;
}

.pagination .total-info {
  margin-left: auto;
  position: absolute;
  right: 1rem;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-right: 0.5rem;
}

.page-size-select {
  background: rgba(15, 23, 42, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  color: #fff;
  padding: 0.4rem 0.5rem;
  font-size: 0.85rem;
  height: 36px;
  cursor: pointer;
  min-width: 70px;
}

.page-size-select:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
}

.page-button {
  background: rgba(255, 255, 255, 0.1);
  color: #cbd5e1;
  padding: 0.5rem 0.75rem;
  font-size: 0.85rem;
}

.page-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.15);
}

.page-info, .total-info {
  color: #94a3b8;
  font-size: 0.9rem;
  background: rgba(148, 163, 184, 0.1);
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
}

/* Add Expense Button */
.add-expense-button-container {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.add-expense-button {
  background: linear-gradient(90deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.85rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
  transition: all 0.3s ease;
}

.add-expense-button:hover {
  background: linear-gradient(90deg, #4338ca 0%, #6d28d9 100%);
  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.3);
  transform: translateY(-1px);
}

.add-expense-button:active {
  transform: translateY(1px);
  box-shadow: 0 2px 8px rgba(79, 70, 229, 0.2);
}

/* Tab Navigation */
.tab-navigation {
  display: flex;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.tab-button {
  background: transparent;
  color: #94a3b8;
  border: none;
  padding: 0.75rem 1.5rem;
  font-size: 1.25rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.tab-button:hover {
  color: #fff;
}

.tab-button.active {
  color: #fff;
}

.tab-button.active::after {
  content: '';
  position: flex;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #4f46e5 0%, #7c3aed 100%);
  border-radius: 2px 2px 0 0;
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

.page-size-control {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1rem;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Responsive adjustments */
@media (max-width: 1024px) {
  .filters {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .expense-container {
    padding: 1rem;
  }
  
  .filters {
    grid-template-columns: 1fr;
  }
  
  .filter-actions {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .filter-actions button {
    width: 100%;
  }
  
  .tab-navigation {
    flex-direction: row;
    overflow-x: auto;
  }
  
  .tab-button {
    padding: 0.75rem 1rem;
    white-space: nowrap;
  }
  
  .pagination {
    flex-direction: column;
    align-items: stretch;
  }
  
  .page-button {
    width: 100%;
  }
  
  .expense-table-header {
    display: none;
  }
  
  .expense-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
    padding: 1rem;
  }
  
  .cell-description {
    font-size: 1rem;
    font-weight: 600;
    margin-bottom: 0.5rem;
  }
  
  .cell-category, .cell-date {
    font-size: 0.85rem;
  }
  
  .cell-amount {
    margin-top: 0.5rem;
  }
  
  .cell-actions {
    text-align: left;
    margin-top: 0.5rem;
  }
}
</style>
