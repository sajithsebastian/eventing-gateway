import time
import os
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from extractor import MobileNumberExtractor
from sender import WhatsAppSender

class NewFileHandler(FileSystemEventHandler):
    def __init__(self, sender):
        self.sender = sender
        self.extractor = MobileNumberExtractor()

    def on_created(self, event):
        if event.is_directory:
            return

        file_path = event.src_path
        filename = os.path.basename(file_path)
        print(f"New file detected: {filename}")

        # Give it a second to make sure the file is fully written
        time.sleep(1)

        mobile_number = self.process_file(file_path)

        if mobile_number:
            print(f"Found mobile number: {mobile_number}. Sending WhatsApp...")
            self.sender.send_file(mobile_number, file_path)
        else:
            print(f"No mobile number found for {filename}")

    def process_file(self, file_path):
        filename = os.path.basename(file_path)

        # 1. Try extracting from filename
        number = self.extractor.extract_from_filename(filename)
        if number:
            return number

        # 2. Try extracting from content based on file type
        ext = os.path.splitext(filename)[1].lower()
        if ext == '.txt':
            with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                return self.extractor.extract_from_text(f.read())
        elif ext == '.pdf':
            return self.extractor.extract_from_pdf(file_path)
        elif ext == '.docx':
            return self.extractor.extract_from_docx(file_path)
        elif ext in ['.jpg', '.jpeg', '.png', '.bmp']:
            return self.extractor.extract_from_image(file_path)

        return None

class FolderWatcher:
    def __init__(self, path_to_watch, sender):
        self.path_to_watch = path_to_watch
        self.sender = sender
        self.event_handler = NewFileHandler(self.sender)
        self.observer = Observer()

    def start(self):
        self.observer.schedule(self.event_handler, self.path_to_watch, recursive=False)
        self.observer.start()
        print(f"Monitoring folder: {self.path_to_watch}")
        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            self.observer.stop()
        self.observer.join()
