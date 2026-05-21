import unittest
from extractor import MobileNumberExtractor

class TestMobileNumberExtractor(unittest.TestCase):
    def test_extract_from_filename(self):
        self.assertEqual(MobileNumberExtractor.extract_from_filename("report_1234567890.pdf"), "+1234567890")
        self.assertEqual(MobileNumberExtractor.extract_from_filename("invoice_+1234567890.docx"), "+1234567890")
        self.assertEqual(MobileNumberExtractor.extract_from_filename("img_123-456-7890.jpg"), "+1234567890")
        self.assertIsNone(MobileNumberExtractor.extract_from_filename("no_number.txt"))

    def test_extract_from_text(self):
        text = "Contact me at +1 123 456 7890 for details."
        self.assertEqual(MobileNumberExtractor.extract_from_text(text), "+11234567890")

        text2 = "My number is (123) 456-7890."
        self.assertEqual(MobileNumberExtractor.extract_from_text(text2), "+1234567890")

    def test_sanitize_number(self):
        self.assertEqual(MobileNumberExtractor.sanitize_number("123-456-7890"), "+1234567890")
        self.assertEqual(MobileNumberExtractor.sanitize_number("+1 123 456 7890"), "+11234567890")

if __name__ == "__main__":
    unittest.main()
