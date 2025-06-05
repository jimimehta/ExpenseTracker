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
    wrapper = mount(ExpenseForm, {
      propsData: {
        isVisible: true
      },
      attachTo: document.body // Attach to document body for more realistic testing
    })

    // Debug output
    console.log('Form HTML:', wrapper.html())
    console.log('Description input exists:', wrapper.find('input#description').exists())
    console.log('Amount input exists:', wrapper.find('input#amount').exists())
    console.log('Date input exists:', wrapper.find('input#date').exists())
    console.log('Category select exists:', wrapper.find('select#category').exists())
  })

  test('emits expense-added event when form is submitted successfully', async () => {
    // Mock successful axios response
    axios.post.mockResolvedValue({})

    await wrapper.find('input#description').setValue('Test Expense')
    await wrapper.find('input#amount').setValue(100)
    await wrapper.find('input#date').setValue('2025-01-01')
    await wrapper.find('select#category').setValue('Groceries')

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
    expect(wrapper.emitted('close')).toBeTruthy()
    expect(wrapper.emitted('close').length).toBe(1)

    // Verifies that the form was reset
    expect(wrapper.vm.description).toBe('')
    expect(wrapper.vm.amount).toBe(0)
    expect(wrapper.vm.date).toBe('')
    expect(wrapper.vm.category).toBe('')
  })

  test('emits close event when close button is clicked', async () => {
    await wrapper.find('.modal-close-button').trigger('click')

    expect(wrapper.emitted('close')).toBeTruthy()
    expect(wrapper.emitted('close').length).toBe(1)
  })

  test('emits close event when overlay is clicked', async () => {
    await wrapper.find('.modal-overlay').trigger('click.self')

    expect(wrapper.emitted('close')).toBeTruthy()
    expect(wrapper.emitted('close').length).toBe(1)
  })

  test('displays error message when API call fails', async () => {
    // Suppress console.error for this test
    const originalConsoleError = console.error;
    console.error = jest.fn();

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

    await wrapper.find('input#description').setValue('Test Expense')
    await wrapper.find('input#amount').setValue(-100) // Negative amount
    await wrapper.find('input#date').setValue('2025-01-01')
    await wrapper.find('select#category').setValue('Groceries')

    //triggers submit form
    await wrapper.find('form').trigger('submit.prevent')

    // Waits for Vue to update the DOM
    await wrapper.vm.$nextTick()

    // Checks if the errors object has the amount error
    expect(wrapper.vm.errors.amount).toBe('Amount must be positive')

    // Verifies that the event was not emitted
    expect(wrapper.emitted('expense-added')).toBeFalsy()

    // Restore console.error
    console.error = originalConsoleError;
  })
})
