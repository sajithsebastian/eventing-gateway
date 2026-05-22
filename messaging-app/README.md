# WhatsApp File Dispatcher

A cross-platform application that monitors a folder for new documents and images, extracts a mobile number from the filename or content, and sends the file to that number via WhatsApp.

## Features
- **Folder Monitoring**: Watches a directory for new files using `watchdog`.
- **Mobile Number Extraction**:
  - From filenames via regex.
  - From Text, PDF, and DOCX contents.
  - From Images (JPG, PNG, etc.) using OCR (EasyOCR).
- **WhatsApp Integration**: Sends messages and media via Twilio API.
- **Cross-Platform**: Compatible with Windows, macOS, and Linux.

## Setup

1. **Install Dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Configure Environment**:
   Copy `.env.example` to `.env` and fill in your Twilio credentials and watch folder path.
   ```bash
   cp .env.example .env
   ```

3. **Run the Application**:
   ```bash
   python main.py --folder ./your-watch-folder
   ```

## Development and Testing

Run tests using `pytest`:
```bash
export PYTHONPATH=\$PYTHONPATH:.
pytest tests/
```

## Building Installers

Use `PyInstaller` to create standalone executables. Refer to `build_instructions.md` for detailed commands for Windows and macOS.
