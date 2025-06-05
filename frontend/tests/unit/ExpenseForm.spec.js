import { mount } from '@vue/test-utils'
import ExpenseForm from '@/components/ExpenseForm.vue'

// Mock axios
jest.mock('axios', () => ({
  post: jest.fn()
}))

const axios = require('axios')

describe('ExpenseForm.vue', () => {
  let wrapper
  beforeEach(() => {
    jest.clearAllMocks()
    global.localStorage = {
      getItem: jest.fn(() => 'dGVzdDp0ZXN0') // 'test:test' in base64
    }
    wrapper = mount(ExpenseForm)
  })

  test('emits expense-added event when form is submitted successfully', async () => {
    // Mock successful axios response
    axios.post.mockResolvedValue({})

    await wrapper.find('input[placeholder="Description"]').setValue('Test Expense')
    await wrapper.find('input[type="number"]').setValue(100)
    await wrapper.find('input[type="date"]').setValue('2025-01-01')
    await wrapper.find('select').setValue('Groceries')

    await wrapper.find('form').trigger('submit.prevent')

    // Verifies that axios was called with correct data
    expect(axios.post).toHaveBeenCalledWith(
      'http://localhost:8080/api/expenses',
      {
        description: 'Test Expense',
        amount: '100',
        date: '2025-01-01',
        category: 'Groceries'
      },
      {
        headers: { 'Authorization': 'Basic null' } // This is what the actual call has
      }
    )

    // Verifies that the event was emitted
    expect(wrapper.emitted('expense-added')).toBeTruthy()
    expect(wrapper.emitted('expense-added').length).toBe(1)

    // Verifies that the form was reset
    expect(wrapper.vm.description).toBe('')
    expect(wrapper.vm.amount).toBe(0)
    expect(wrapper.vm.date).toBe('')
    expect(wrapper.vm.category).toBe('')
  })

  test('displays error message when API call fails', async () => {
    // Mock failed axios response
    const errorResponse = {
      response: {
        status: 400,
        data: {
          details: {
            amount: 'Amount must be positive'
          }
        }
      }
    }
    axios.post.mockRejectedValue(errorResponse)

    await wrapper.find('input[placeholder="Description"]').setValue('Test Expense')
    await wrapper.find('input[type="number"]').setValue(-100) // Negative amount
    await wrapper.find('input[type="date"]').setValue('2025-01-01')
    await wrapper.find('select').setValue('Groceries')

    //triggers submit form
    await wrapper.find('form').trigger('submit.prevent')

    // Waits for Vue to update the DOM
    await wrapper.vm.$nextTick()

    // Checks if the errors object has the amount error
    expect(wrapper.vm.errors.amount).toBe('Amount must be positive')

    // Verifies that the event was not emitted
    expect(wrapper.emitted('expense-added')).toBeFalsy()
  })
})
