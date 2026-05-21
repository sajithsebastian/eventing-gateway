import unittest
from unittest.mock import MagicMock, patch
from sender import WhatsAppSender

class TestWhatsAppSender(unittest.TestCase):
    @patch('sender.Client')
    def test_send_file_mock_mode(self, mock_client):
        # Case when credentials are not provided
        sender = WhatsAppSender(account_sid=None, auth_token=None)
        self.assertIsNone(sender.client)

        result = sender.send_file("+1234567890", "test.txt")
        self.assertTrue(result)

    @patch('sender.Client')
    def test_send_file_twilio_mode(self, mock_client_class):
        # Case when credentials are provided
        mock_client_instance = mock_client_class.return_value
        mock_messages = MagicMock()
        mock_client_instance.messages = mock_messages

        sender = WhatsAppSender(account_sid="AC123", auth_token="token", from_whatsapp_number="+14155238886")
        self.assertIsNotNone(sender.client)

        result = sender.send_file("+1234567890", "test.txt")

        self.assertTrue(result)
        mock_messages.create.assert_called_once()
        args, kwargs = mock_messages.create.call_args
        self.assertIn("test.txt", kwargs['body'])
        self.assertEqual(kwargs['to'], "whatsapp:+1234567890")
        self.assertEqual(kwargs['from_'], "whatsapp:+14155238886")

if __name__ == "__main__":
    unittest.main()
