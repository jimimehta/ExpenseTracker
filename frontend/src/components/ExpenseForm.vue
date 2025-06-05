<template>
  <div class="form-container">

    <h2>Add Expense</h2>

    <form @submit.prevent="addExpense">
      <div class="form-group">
      <input v-model="description" placeholder="Description" required @input="errors.description = ''" />

      <div v-if="errors.description" class="error-message">{{ errors.description }}</div>

      <input v-model="amount" type="number" placeholder="Amount" required @input="errors.amount = ''" />

      <div v-if="errors.amount" class="error-message">{{ errors.amount }}</div>

      <input v-model="date" type="date" required @input="clearDateErrors"/>

      <div v-if="errors.date || errors.dateValid" class="error-message">
        {{ errors.date || errors.dateValid }}
      </div>

      <select v-model="category" required @change="errors.category = ''">
        <option value="" disabled>Select Category</option>
        <option value="Groceries">Groceries</option>
        <option value="Utilities">Utilities</option>
        <option value="Travel">Travel</option>
        <option value="Shopping">Shopping</option>
        <option value="Bills">Bills</option>
        <option value="Other">Other</option>
      </select>

      <div v-if="errors.category" class="error-message">{{ errors.category }}</div>

      <button type="submit">Add</button>
      </div>
    </form>

    <!-- Error message display-->
    <div v-if="generalError" class="error-message">
      {{ generalError }}
    </div>

  </div>


</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      description: '',
      amount: 0,
      date: '',
      category: '',
      errors: {},
      generalError: ''
    };
  },
  methods: {
    clearErrors() {
      this.errors = {};
      this.generalError = '';
    },
    clearDateErrors() {
      this.errors.date = '';
      this.errors.dateValid = '';
    },
    addExpense() {
      this.clearErrors();
      const auth = localStorage.getItem('auth');
      axios.post('http://localhost:8080/api/expenses', {
        description: this.description,
        amount: this.amount,
        date: this.date,
        category: this.category
      }, {
        headers: { 'Authorization': `Basic ${auth}` }
      })
      .then(() => {
        this.$emit('expense-added');
        this.description = '';
        this.amount = 0;
        this.date = '';
        this.category = '';
      })
      .catch(error => {
        if (error.response) {
          // This Handles validation errors
          if (error.response.status === 400 && error.response.data.details) {
            this.errors = error.response.data.details;
          } else {
            // This Handles other errors
            this.generalError = error.response.data.message || 'Error has occurred while saving the expense';
          }
        } else {
          this.generalError = 'Network error! Please try again later!';
        }
        console.error(error);
      });
    }
  }
};
</script>

<style scoped>
.form-container {
  max-width: 500px;
  margin: auto;
}
.form-group {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
}
input, select {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 5px;
}
button {
  padding: 0.6rem 1.2rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #0056b3;
}
.error-message {
  color: #dc3545;
  font-size: 0.9em;
  margin-top: 0.25rem;
}
</style>