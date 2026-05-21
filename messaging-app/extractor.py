import re
import os
from pypdf import PdfReader
from docx import Document
import easyocr

class MobileNumberExtractor:
    _reader = None

    @classmethod
    def get_ocr_reader(cls):
        if cls._reader is None:
            # Initialize the EasyOCR reader (it will download models on first run)
            cls._reader = easyocr.Reader(['en'])
        return cls._reader

    # Regex for various mobile number formats
    # Matches formats like: +1234567890, 1234567890, 123-456-7890, (123) 456-7890
    PHONE_REGEX = re.compile(r'(\+?\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}')

    @classmethod
    def extract_from_filename(cls, filename):
        """Extract mobile number from the filename."""
        match = cls.PHONE_REGEX.search(filename)
        if match:
            return cls.sanitize_number(match.group())
        return None

    @classmethod
    def extract_from_text(cls, text):
        """Extract mobile number from a string of text."""
        if not text:
            return None
        match = cls.PHONE_REGEX.search(text)
        if match:
            return cls.sanitize_number(match.group())
        return None

    @classmethod
    def extract_from_pdf(cls, file_path):
        """Extract text from PDF and find mobile number."""
        try:
            reader = PdfReader(file_path)
            text = ""
            for page in reader.pages:
                text += page.extract_text() or ""
            return cls.extract_from_text(text)
        except Exception as e:
            print(f"Error reading PDF {file_path}: {e}")
            return None

    @classmethod
    def extract_from_docx(cls, file_path):
        """Extract text from Word document and find mobile number."""
        try:
            doc = Document(file_path)
            text = "\n".join([para.text for para in doc.paragraphs])
            return cls.extract_from_text(text)
        except Exception as e:
            print(f"Error reading DOCX {file_path}: {e}")
            return None

    @classmethod
    def extract_from_image(cls, file_path):
        """Extract text from image using OCR and find mobile number."""
        try:
            reader = cls.get_ocr_reader()
            results = reader.readtext(file_path)
            text = " ".join([res[1] for res in results])
            return cls.extract_from_text(text)
        except Exception as e:
            print(f"Error performing OCR on {file_path}: {e}")
            return None

    @staticmethod
    def sanitize_number(number):
        """Sanitize the extracted number to a clean format for WhatsApp (e.g., +1234567890)."""
        # Remove all non-digit characters
        clean_number = re.sub(r'\D', '', number)
        # Ensure it starts with a plus
        if not number.startswith('+'):
            return "+" + clean_number
        return "+" + clean_number
