import os
from twilio.rest import Client

class WhatsAppSender:
    def __init__(self, account_sid=None, auth_token=None, from_whatsapp_number=None):
        self.account_sid = account_sid or os.getenv('TWILIO_ACCOUNT_SID')
        self.auth_token = auth_token or os.getenv('TWILIO_AUTH_TOKEN')
        self.from_number = from_whatsapp_number or os.getenv('TWILIO_FROM_NUMBER')

        if self.account_sid and self.auth_token:
            self.client = Client(self.account_sid, self.auth_token)
        else:
            self.client = None
            print("Warning: Twilio credentials not provided. Sender will run in mock mode.")

    def send_file(self, to_number, file_path, message_text="Here is your document."):
        """Send a file via WhatsApp using Twilio."""
        if not self.client:
            print(f"[MOCK] Sending {file_path} to {to_number} with message: {message_text}")
            return True

        try:
            # Twilio requires the media_url to be publicly accessible.
            # Local files must be uploaded to a storage provider (like AWS S3) first.
            # Users should configure their own upload logic here or provide a URL.
            media_url = os.getenv('MEDIA_BASE_URL')

            if media_url:
                full_media_url = f"{media_url.rstrip('/')}/{os.path.basename(file_path)}"
                message = self.client.messages.create(
                    body=message_text,
                    from_=f"whatsapp:{self.from_number}",
                    to=f"whatsapp:{to_number}",
                    media_url=[full_media_url]
                )
            else:
                # Fallback: Send text notification if no media hosting is configured
                print("Warning: MEDIA_BASE_URL not set. Sending text notification instead of file.")
                message = self.client.messages.create(
                    body=f"{message_text} (File attached: {os.path.basename(file_path)}) - [Note: Configure MEDIA_BASE_URL to send actual files]",
                    from_=f"whatsapp:{self.from_number}",
                    to=f"whatsapp:{to_number}"
                )

            print(f"Message sent successfully! SID: {message.sid}")
            return True
        except Exception as e:
            print(f"Error sending WhatsApp message: {e}")
            return False
