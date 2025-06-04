<template>
  <div>

    <h2>Add Expense</h2>

    <form @submit.prevent="addExpense">

      <input v-model="description" placeholder="Description" required />

      <input v-model="amount" type="number" placeholder="Amount" required />

      <input v-model="date" type="date" required />

      <select v-model="category" required>
        <option value="" disabled>Select Category</option>
        <option value="Groceries">Groceries</option>
        <option value="Utilities">Utilities</option>
        <option value="Travel">Travel</option>
        <option value="Shopping">Shopping</option>
        <option value="Bills">Bills</option>
        <option value="Other">Other</option>
      </select>

      <button type="submit">Add</button>

    </form>

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
      category: ''
    };
  },
  methods: {
    addExpense() {
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
      .catch(error => console.error(error));
    }
  }
};
</script>