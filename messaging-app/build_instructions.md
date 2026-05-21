# Building the WhatsApp File Dispatcher Installer

This application can be compiled into a standalone executable for Windows and Mac using `PyInstaller`.

## Prerequisites
- Python 3.12+
- All dependencies installed: `pip install -r requirements.txt`

## Build Instructions

### Windows
Run the following command in PowerShell or Command Prompt:
```bash
pyinstaller --onefile --windowed --name WhatsAppDispatcher --add-data ".env.example;." main.py
```
This will generate `WhatsAppDispatcher.exe` in the `dist/` directory.

### macOS
Run the following command in Terminal:
```bash
pyinstaller --onefile --windowed --name WhatsAppDispatcher --add-data ".env.example:." main.py
```
This will generate `WhatsAppDispatcher.app` in the `dist/` directory.

## Note on OCR
`easyocr` and `torch` are large libraries. The first time you run the compiled application, it may take some time to initialize as it might download the required OCR models to your home directory (`~/.EasyOCR/`).

If you want to bundle the models with the app, you will need to add them to the `--add-data` flag in the PyInstaller command.
