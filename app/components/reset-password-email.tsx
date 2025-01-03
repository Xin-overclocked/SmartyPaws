export function ResetPasswordEmail({
  resetLink = "https://example.com/reset-password"
}: {
  resetLink?: string
}) {
  return (
    <div className="p-6 max-w-md mx-auto bg-black text-white">
      <h2 className="text-xl mb-4">Reset your password for recoveryHope</h2>
      <p className="mb-4">Hello,</p>
      <p className="mb-4">
        Follow this link to reset your recoveryHope account password:
      </p>
      <a
        href={resetLink}
        className="text-blue-400 break-all mb-4 block"
      >
        {resetLink}
      </a>
      <p className="mb-4">
        If you didn't ask to reset your password, you can ignore this email.
      </p>
      <p className="mb-2">Thanks,</p>
      <p>Your recoveryHope team</p>
    </div>
  )
}

