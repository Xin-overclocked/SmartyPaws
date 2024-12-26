'use server'

import { z } from 'zod'

const emailSchema = z.string().email()
const passwordSchema = z.string().min(8)

export async function sendResetEmail(formData: FormData) {
  const email = formData.get('email')

  try {
      emailSchema.parse(email)
    } catch (error) {
      return { success: false, message: 'Invalid email address' }
    }

    try {
      // In a real app, you would:
      // 1. Generate a secure token
      // 2. Store it in your database with an expiration
      // 3. Send an actual email
      await new Promise(resolve => setTimeout(resolve, 1000)) // Simulate API call

      return {
        success: true,
        message: 'If an account exists with this email, you will receive a password reset link.'
      }
    } catch (error) {
      console.error('Failed to send reset email:', error)
      return { success: false, message: 'Failed to send reset email. Please try again.' }
    }
  }

  export async function resetPassword(formData: FormData) {
    const password = formData.get('password')

    try {
      passwordSchema.parse(password)
    } catch (error) {
      return { success: false, message: 'Invalid password. Must be at least 8 characters long.' }
    }

    try {
      await new Promise(resolve => setTimeout(resolve, 1000)) // Simulate API call

      return { success: true }
    } catch (error) {
      console.error('Failed to reset password:', error)
      return { success: false, message: 'Failed to reset password. Please try again.' }
    }
  }

