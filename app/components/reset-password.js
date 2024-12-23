'use server';

export async function sendResetEmail(formData) {
  const email = formData.get('email');
  await new Promise(resolve => setTimeout(resolve, 1000)); // Simulated API call
  return {
    success: true,
    message: 'If an account exists with this email, you will receive a password reset link.',
  };
}

export async function resetPassword(formData) {
  const password = formData.get('password');
  await new Promise(resolve => setTimeout(resolve, 1000)); // Simulated API call
  return { success: true };
}