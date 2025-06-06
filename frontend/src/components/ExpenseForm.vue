<template>
  <div v-show="isVisible" class="modal-overlay" @click.self="closeModal">
    <div class="auth-card modal-card">
      <div class="modal-close-button" @click="closeModal">
        <svg xmlns="http://www.w3.org/2000/svg" class="close-icon" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
        </svg>
      </div>

      <div class="auth-header">
        <div class="logo-circle">
          <svg v-if="!isEditMode" xmlns="http://www.w3.org/2000/svg" class="add-icon" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" class="add-icon" viewBox="0 0 20 20" fill="currentColor">
            <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
          </svg>
        </div>
        <h2>{{ formTitle }}</h2>
        <p class="subtitle">Enter the details of your expense below</p>
      </div>

      <div class="auth-form-container">
        <form @submit.prevent="submitExpense">
          <div class="form-field">
            <label for="description" class="input-label">Description</label>
            <input 
              id="description"
              v-model="description" 
              placeholder="Enter expense description" 
              required 
              @input="errors.description = ''" 
              :class="{'input-error': errors.description}"
            />
            <div v-if="errors.description" class="error-message">{{ errors.description }}</div>
          </div>

          <div class="form-field">
            <label for="amount" class="input-label">Amount</label>
            <input 
              id="amount"
              v-model="amount" 
              type="number" 
              step="0.01" 
              placeholder="Enter amount" 
              required 
              @input="errors.amount = ''" 
              :class="{'input-error': errors.amount}"
            />
            <div v-if="errors.amount" class="error-message">{{ errors.amount }}</div>
          </div>

          <div class="form-field">
            <label for="date" class="input-label">Date</label>
            <input 
              id="date"
              v-model="date" 
              type="date" 
              required 
              @input="clearDateErrors"
              :class="{'input-error': errors.date || errors.dateValid}"
            />
            <div v-if="errors.date || errors.dateValid" class="error-message">
              {{ errors.date || errors.dateValid }}
            </div>
          </div>

          <div class="form-field">
            <label for="category" class="input-label">Category</label>
            <select 
              id="category"
              v-model="category" 
              required 
              @change="errors.category = ''"
              :class="{'input-error': errors.category}"
            >
              <option value="" disabled>Select Category</option>
              <option value="Groceries">Groceries</option>
              <option value="Utilities">Utilities</option>
              <option value="Travel">Travel</option>
              <option value="Shopping">Shopping</option>
              <option value="Bills">Bills</option>
              <option value="Other">Other</option>
            </select>
            <div v-if="errors.category" class="error-message">{{ errors.category }}</div>
          </div>

          <div class="form-actions">
            <button type="submit" class="auth-button">
              <svg v-if="!isEditMode" xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 20 20" fill="currentColor">
                <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
              </svg>
              {{ isEditMode ? 'Update Expense' : 'Add Expense' }}
            </button>
          </div>
        </form>
      </div>

      <!-- Error message display-->
      <transition 
        enter-active-class="transition duration-300 ease-out" 
        enter-from-class="transform -translate-y-2 opacity-0" 
        enter-to-class="transform translate-y-0 opacity-100"
        leave-active-class="transition duration-200 ease-in" 
        leave-from-class="transform translate-y-0 opacity-100" 
        leave-to-class="transform -translate-y-2 opacity-0"
      >
        <div v-if="generalError" class="general-error">
          <div class="error-content">
            <svg xmlns="http://www.w3.org/2000/svg" class="error-icon" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
            </svg>
            <p>{{ generalError }}</p>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    expenseToEdit: {
      type: Object,
      default: null
    }
  },
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
  computed: {
    isEditMode() {
      return this.expenseToEdit !== null;
    },
    formTitle() {
      return this.isEditMode ? 'Edit Expense' : 'Add New Expense';
    }
  },
  watch: {
    expenseToEdit(expense) {
      if (expense) {
        this.description = expense.description;
        this.amount = expense.amount;
        this.date = expense.date;
        this.category = expense.category;
      } else {
        this.resetForm();
      }
    }
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
    closeModal() {
      this.$emit('close');
    },
    resetForm() {
      this.description = '';
      this.amount = 0;
      this.date = '';
      this.category = '';
    },
    submitExpense() {
      this.clearErrors();
      const auth = localStorage.getItem('auth');
      const expenseData = {
        description: this.description,
        amount: this.amount,
        date: this.date,
        category: this.category
      };

      const config = {
        headers: { 'Authorization': `Basic ${auth}` }
      };

      let request;
      if (this.isEditMode) {
        // Use PUT for updating
        request = axios.put(
          `http://localhost:8080/api/expenses/${this.expenseToEdit.id}`,
          expenseData,
          config
        );
      } else {
        // Use POST for creating
        request = axios.post(
          'http://localhost:8080/api/expenses',
          expenseData,
          config
        );
      }

      request
        .then(() => {
          this.$emit(this.isEditMode ? 'expense-updated' : 'expense-added');
          this.resetForm();
          this.closeModal();
        })
      .catch(error => {
        if (error.response) {
          // This Handles validation errors
          if (error.response.status === 400 && error.response.data.details) {
            this.errors = error.response.data.details;
          } else {
            // This Handles other errors
            // Always use the actual error message from the backend when available
            if (error.response.data) {
              // Try to extract the error message from various possible locations in the response
              if (error.response.data.message) {
                this.generalError = error.response.data.message;
              } else if (error.response.data.error) {
                this.generalError = error.response.data.error;
              } else if (typeof error.response.data === 'string') {
                this.generalError = error.response.data;
              } else if (error.response.data.errors && Array.isArray(error.response.data.errors)) {
                // Some APIs return errors as an array
                this.generalError = error.response.data.errors.join(', ');
              } else if (typeof error.response.data === 'object') {
                // Try to extract any property that might contain an error message
                const errorObj = error.response.data;
                const errorMessage = Object.keys(errorObj)
                  .filter(key => typeof errorObj[key] === 'string')
                  .map(key => errorObj[key])
                  .join(', ');

                if (errorMessage) {
                  this.generalError = errorMessage;
                } else {
                  this.generalError = 'Error has occurred while saving the expense';
                }
              } else {
                this.generalError = 'Error has occurred while saving the expense';
              }
            } else {
              this.generalError = 'Error has occurred while saving the expense';
            }

            // Log the full error response for debugging
            console.log('Error response:', error.response);
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
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 1rem;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.2s ease;
}

.modal-card {
  position: relative;
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  overflow-y: hidden;
  animation: slideIn 0.3s ease;
  display: flex;
  flex-direction: column;
}

.modal-close-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s ease;
}

.modal-close-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

.close-icon {
  width: 20px;
  height: 20px;
  color: #cbd5e1;
}

.auth-card {
  background: #1e293b;
  border-radius: 16px;
  padding: 0;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25), 0 4px 8px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.08);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  color: #cbd5e1;
  overflow: hidden;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.auth-header {
  background: linear-gradient(135deg, rgba(79, 70, 229, 0.1) 0%, rgba(124, 58, 237, 0.1) 100%);
  padding: 2rem;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.logo-circle {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.2rem;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
}

.add-icon {
  color: white;
  width: 30px;
  height: 30px;
}

h2 {
  color: #fff;
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0 0 0.5rem;
  letter-spacing: 0.5px;
}

.subtitle {
  color: #94a3b8;
  font-size: 1rem;
  margin: 0;
  font-weight: 400;
}

.auth-form-container {
  padding: 2rem;
  overflow-y: auto;
  max-height: calc(90vh - 180px); /* Subtract header height */
  scrollbar-width: thin;
  scrollbar-color: rgba(255, 255, 255, 0.2) rgba(15, 23, 42, 0.3);
}

.auth-form-container::-webkit-scrollbar {
  width: 6px;
}

.auth-form-container::-webkit-scrollbar-track {
  background: rgba(15, 23, 42, 0.3);
  border-radius: 10px;
}

.auth-form-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
}

.auth-form-container::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

.form-field {
  margin-bottom: 1.5rem;
}

.input-label {
  font-size: 0.9rem;
  color: #cbd5e1;
  margin-bottom: 0.6rem;
  display: block;
  font-weight: 500;
}

input, select {
  width: 100%;
  padding: 0.85rem 1rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  font-size: 1rem;
  background: rgba(15, 23, 42, 0.7);
  color: #fff;
  transition: border-color 0.3s, box-shadow 0.3s;
  box-sizing: border-box;
  max-width: 100%;
}

input::placeholder, select::placeholder {
  color: #64748b;
}

input:focus, select:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
}

.input-error {
  border-color: #ef4444 !important;
  background: rgba(239, 68, 68, 0.1) !important;
}

.form-actions {
  margin-top: 2rem;
}

.auth-button {
  width: 100%;
  padding: 1rem 1.5rem;
  background: linear-gradient(90deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.auth-button:hover {
  background: linear-gradient(90deg, #4338ca 0%, #6d28d9 100%);
  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.3);
  transform: translateY(-1px);
}

.auth-button:active {
  transform: translateY(1px);
  box-shadow: 0 2px 8px rgba(79, 70, 229, 0.2);
}

.icon {
  width: 1.25rem;
  height: 1.25rem;
}

.error-message {
  color: #ef4444;
  font-size: 0.85rem;
  margin-top: 0.4rem;
  display: flex;
  align-items: center;
}

.error-message::before {
  content: "⚠️";
  margin-right: 0.3rem;
  font-size: 0.85rem;
}

.general-error {
  margin-top: 1.2rem;
  color: #fff;
  background: rgba(239, 68, 68, 0.8);
  padding: 0.85rem 1rem;
  border-radius: 8px;
  font-size: 0.95rem;
  text-align: left;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
  animation: shake 0.3s;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.error-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.error-icon {
  width: 1.25rem;
  height: 1.25rem;
  flex-shrink: 0;
}

@keyframes shake {
  0% { transform: translateX(0);}
  20% { transform: translateX(-5px);}
  40% { transform: translateX(5px);}
  60% { transform: translateX(-5px);}
  80% { transform: translateX(5px);}
  100% { transform: translateX(0);}
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .auth-header {
    padding: 1.5rem 1rem;
  }
  
  .auth-form-container {
    padding: 1.5rem 1rem;
  }
}
</style>
