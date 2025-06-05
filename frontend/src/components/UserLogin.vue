<template>
  <div class="form-container">
    <div class="login-card">
      <div class="logo-circle">
        <span class="logo-text">💸</span>
      </div>
      <h1>Expense Tracker</h1>
      <h2>Login to Your Account</h2>

      <div class="form-wrapper">
        <form @submit.prevent="login" class="login-form">
          <div class="form-group">
            <div class="form-field">
              <label for="username" class="input-label">Username</label>
              <input
                id="username"
                v-model="username"
                :class="{'input-error': errorField === 'username'}"
                placeholder="Enter your username"
                required
                @input="clearErrors"
                autocomplete="username"
                type="text"
              />
            </div>

            <div class="form-field">
              <label for="password" class="input-label">Password</label>
              <input
                id="password"
                v-model="password"
                :class="{'input-error': errorField === 'password'}"
                type="password"
                placeholder="Enter your password"
                required
                @input="clearErrors"
                autocomplete="current-password"
              />
            </div>

            <div class="form-actions">
              <button type="submit" :disabled="isLoading" class="login-button">
                <svg v-if="!isLoading" xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M3 3a1 1 0 011 1v12a1 1 0 11-2 0V4a1 1 0 011-1zm7.707 3.293a1 1 0 010 1.414L9.414 9H17a1 1 0 110 2H9.414l1.293 1.293a1 1 0 01-1.414 1.414l-3-3a1 1 0 010-1.414l3-3a1 1 0 011.414 0z" clip-rule="evenodd" />
                </svg>
                <span v-if="isLoading" class="spinner"></span>
                {{ isLoading ? 'Logging in...' : 'Login' }}
              </button>
            </div>
          </div>
        </form>
      </div>

      <transition 
        enter-active-class="transition duration-300 ease-out" 
        enter-from-class="transform -translate-y-2 opacity-0" 
        enter-to-class="transform translate-y-0 opacity-100"
        leave-active-class="transition duration-200 ease-in" 
        leave-from-class="transform translate-y-0 opacity-100" 
        leave-to-class="transform -translate-y-2 opacity-0"
      >
        <div v-if="errorMessage" class="error-message">
          <div class="error-content">
            <svg xmlns="http://www.w3.org/2000/svg" class="error-icon" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
            </svg>
            <p>{{ errorMessage }}</p>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      isLoading: false,
      errorMessage: '',
      errorField: ''
    };
  },
  methods: {
    clearErrors() {
      this.errorMessage = '';
      this.errorField = '';
    },
    validateForm() {
      if (!this.username.trim()) {
        this.errorMessage = 'Username is required';
        this.errorField = 'username';
        return false;
      }
      if (!this.password) {
        this.errorMessage = 'Password is required';
        this.errorField = 'password';
        return false;
      }
      return true;
    },
    login() {
      if (!this.validateForm()) return;
      this.isLoading = true;
      this.errorMessage = '';
      this.errorField = '';
      setTimeout(() => {
        try {
          // Simulate login failure for demonstration
          if (this.username !== 'admin' || this.password !== 'admin') {
            this.errorMessage = 'Invalid username or password.';
            this.errorField = 'username';
            this.isLoading = false;
            return;
          }
          localStorage.removeItem('auth');
          const credentials = btoa(`${this.username}:${this.password}`);
          localStorage.setItem('auth', credentials);
          this.$router.push('/expenses');
        } catch (error) {
          console.error('Login error:', error);
          this.errorMessage = 'An error occurred during login. Please try again.';
        } finally {
          this.isLoading = false;
        }
      }, 800);
    }
  }
};
</script>

<style scoped>
.form-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  padding: 1rem;
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 2.5rem 2rem 2rem 2rem;
  border-radius: 18px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25), 0 4px 8px rgba(0, 0, 0, 0.15);
  background: #1e293b;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-circle {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.2rem;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
}

.logo-text {
  font-size: 2rem;
  color: #fff;
}

h1 {
  color: #fff;
  text-align: center;
  margin-bottom: 0.2rem;
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: 0.5px;
}

h2 {
  color: #94a3b8;
  text-align: center;
  margin-bottom: 1.5rem;
  font-weight: 400;
  font-size: 1rem;
  letter-spacing: 0.5px;
}

.form-wrapper {
  background: rgba(15, 23, 42, 0.5);
  border-radius: 8px;
  padding: 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.05);
  width: 100%;
}

.form-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-field {
  background: rgba(15, 23, 42, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  padding: 1rem;
  transition: all 0.2s ease;
}

.form-field:hover {
  border-color: rgba(79, 70, 229, 0.3);
  box-shadow: 0 0 0 1px rgba(79, 70, 229, 0.1);
}

.input-label {
  font-size: 0.9rem;
  color: #cbd5e1;
  margin-bottom: 0.6rem;
  display: block;
  font-weight: 500;
}

input[type="text"],
input[type="password"] {
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

.input-error {
  border-color: #ef4444 !important;
  background: rgba(239, 68, 68, 0.1) !important;
}

input[type="text"]::placeholder,
input[type="password"]::placeholder {
  color: #64748b;
}

input[type="text"]:focus,
input[type="password"]:focus {
  border-color: #4f46e5;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
}

.form-actions {
  padding-top: 0.5rem;
}

.login-button {
  width: 100%;
  padding: 0.85rem 0;
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
  gap: 0.7em;
}

.login-button:hover:not(:disabled) {
  background: linear-gradient(90deg, #4338ca 0%, #6d28d9 100%);
  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.3);
  transform: translateY(-1px);
}

.login-button:active:not(:disabled) {
  transform: translateY(1px);
  box-shadow: 0 2px 8px rgba(79, 70, 229, 0.2);
}

.login-button:disabled {
  background: #4b5563;
  cursor: not-allowed;
  box-shadow: none;
}

.icon {
  width: 1.25rem;
  height: 1.25rem;
}

.spinner {
  border: 2.5px solid rgba(255, 255, 255, 0.2);
  border-top: 2.5px solid #ffffff;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  animation: spin 0.7s linear infinite;
  display: inline-block;
  vertical-align: middle;
}

@keyframes spin {
  0% { transform: rotate(0deg);}
  100% { transform: rotate(360deg);}
}

.error-message {
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
  width: 100%;
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
</style>
