'use client'

import { useState, useRef, useEffect } from 'react'
import { ArrowLeft, Eye, EyeOff } from 'lucide-react'
import { useActionState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { sendResetEmail, resetPassword } from '@/app/actions/reset-password'

export function ForgotPasswordForm() {
  const [step, setStep] = useState<'email' | 'emailSent' | 'reset' | 'success'>('email')
  const [showPassword, setShowPassword] = useState(false)
  const [emailState, emailAction] = useActionState(sendResetEmail)
  const [resetState, resetAction] = useActionState(resetPassword)
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const focusRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
      if (focusRef.current){
          focusRef.current.focus()
      }
  }, [step])

  const validateEmail = (email: string) => {
      const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
          return re.test(String(email).toLowerCase())
  }

  const validatePassword = (password: string) => {
      return password.length >= 8
    }

  if (step === 'emailSent') {
    return (
      <div className="p-6 space-y-4">
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setStep('email')}
            aria-label="Go back"
          >
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <h2 className="text-xl font-semibold">Reset your password for</h2>
        </div>
        <p className="font-semibold">{email}</p>
        <p className="text-muted-foreground">
          Follow this link to reset your recoveryHope account password. If you didn't ask to reset your password, you can ignore this email.
        </p>
        <Button
          onClick={() => setStep('reset')}
          className="w-full bg-blue-600 hover:bg-blue-700"
        >
          Open email app
        </Button>
      </div>
    )
  }

  if (step === 'reset') {
    return (
      <div className="p-6 space-y-4">
        <h2 className="text-xl font-semibold">Reset your password</h2>
        <p className="text-muted-foreground">for {email}</p>
        <form
          onSubmit={async (e) => {
              e.preventDefault()
              const formData = new FormData(e.currentTarget)
              const password = formData.get('password') as string
              if (!validatePassword(password)) {
                setError('Password must be at least 8 characters long')
                return
              }
              setError('')
              const result = await resetAction(formData)
              if (result?.success) {
                setStep('success')
              } else {
                setError('Failed to reset password. Please try again.')
              }
            }}
            className="space-y-4"
          >
            <div className="space-y-2 relative">
              <Input
                ref={focusRef}
                type={showPassword ? "text" : "password"}
                name="password"
                placeholder="New password"
                required
                aria-invalid={error ? "true" : "false"}
                aria-describedby="password-error"
              />
              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="absolute right-2 top-1/2 -translate-y-1/2"
                onClick={() => setShowPassword(!showPassword)}
                aria-label={showPassword ? "Hide password" : "Show password"}
              >
                {showPassword ? (
                  <EyeOff className="h-4 w-4" />
                ) : (
                  <Eye className="h-4 w-4" />
                )}
              </Button>
            </div>
            {error && <p id="password-error" className="text-red-500 text-sm">{error}</p>}
            <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700">
              {resetState.pending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Resetting...
                </>
              ) : (
                'Save'
              )}
            </Button>
          </form>
        </div>
      )
    }

    if (step === 'success') {
      return (
        <div className="p-6 space-y-4">
          <h2 className="text-xl font-semibold">Password changed</h2>
          <p className="text-muted-foreground">
            You can now sign in with your new password
          </p>
          <Button
            onClick={() => setStep('email')}
            className="w-full bg-blue-600 hover:bg-blue-700"
          >
            Sign in
          </Button>
        </div>
      )
    }

    return (
      <div className="p-6 space-y-4">
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="icon"
            aria-label="Go back"
          >
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <h2 className="text-xl font-semibold">Forgot Password</h2>
        </div>
        <p className="text-muted-foreground">
          Please enter your email to reset the password
        </p>
        <form
          onSubmit={async (e) => {
            e.preventDefault()
            const formData = new FormData(e.currentTarget)
            const email = formData.get('email') as string
            if (!validateEmail(email)) {
              setError('Please enter a valid email address')
              return
            }
            setError('')
            const result = await emailAction(formData)
            if (result?.success) {
              setEmail(email)
              setStep('emailSent')
            } else {
              setError('Failed to send reset email. Please try again.')
            }
          }}
          className="space-y-4"
        >
          <div className="space-y-2">
            <Input
              ref={focusRef}
              type="email"
              name="email"
              placeholder="Enter your email"
              required
              onChange={(e) => setEmail(e.target.value)}
              aria-invalid={error ? "true" : "false"}
              aria-describedby="email-error"
            />
          </div>
          {error && <p id="email-error" className="text-red-500 text-sm">{error}</p>}
          <Button type="submit" className="w-full bg-purple-600 hover:bg-purple-700">
            {emailState.pending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Sending...
              </>
            ) : (
              'Reset Password'
            )}
          </Button>
        </form>
        {emailState?.message && (
          <p className="text-sm text-muted-foreground text-center">
            {emailState.message}
          </p>
        )}
      </div>
    )
  }

