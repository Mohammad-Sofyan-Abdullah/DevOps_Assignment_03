from fastapi import FastAPI, HTTPException
from fastapi.staticfiles import StaticFiles
from fastapi.responses import HTMLResponse
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, List
from motor.motor_asyncio import AsyncIOMotorClient
from bson import ObjectId
import os

app = FastAPI()

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# MongoDB connection
MONGODB_URL = os.getenv("MONGODB_URL", "mongodb://localhost:27017")
client = AsyncIOMotorClient(MONGODB_URL)
db = client.crud_database

# Mount static files
app.mount("/static", StaticFiles(directory="static"), name="static")

# Pydantic models
class Student(BaseModel):
    name: str
    age: int
    email: str
    course: str
    grade: Optional[str] = None

class StudentUpdate(BaseModel):
    name: Optional[str] = None
    age: Optional[int] = None
    email: Optional[str] = None
    course: Optional[str] = None
    grade: Optional[str] = None

class Book(BaseModel):
    title: str
    author: str
    isbn: str
    year: int
    available: bool = True

class BookUpdate(BaseModel):
    title: Optional[str] = None
    author: Optional[str] = None
    isbn: Optional[str] = None
    year: Optional[int] = None
    available: Optional[bool] = None

# Helper function to convert ObjectId to string
def student_helper(student) -> dict:
    return {
        "id": str(student["_id"]),
        "name": student["name"],
        "age": student["age"],
        "email": student["email"],
        "course": student["course"],
        "grade": student.get("grade", "")
    }

def book_helper(book) -> dict:
    return {
        "id": str(book["_id"]),
        "title": book["title"],
        "author": book["author"],
        "isbn": book["isbn"],
        "year": book["year"],
        "available": book.get("available", True)
    }

# Serve HTML page
@app.get("/", response_class=HTMLResponse)
async def read_root():
    with open("templates/index.html", "r", encoding="utf-8") as f:
        return f.read()

# ==================== STUDENT CRUD OPERATIONS ====================

# 1. Create Student
@app.post("/api/students", response_model=dict)
async def create_student(student: Student):
    student_dict = student.dict()
    result = await db.students.insert_one(student_dict)
    new_student = await db.students.find_one({"_id": result.inserted_id})
    return student_helper(new_student)

# 2. Read All Students
@app.get("/api/students", response_model=List[dict])
async def get_all_students():
    students = []
    async for student in db.students.find():
        students.append(student_helper(student))
    return students

# 3. Read Single Student
@app.get("/api/students/{student_id}", response_model=dict)
async def get_student(student_id: str):
    if not ObjectId.is_valid(student_id):
        raise HTTPException(status_code=400, detail="Invalid student ID")
    student = await db.students.find_one({"_id": ObjectId(student_id)})
    if student:
        return student_helper(student)
    raise HTTPException(status_code=404, detail="Student not found")

# 4. Update Student
@app.put("/api/students/{student_id}", response_model=dict)
async def update_student(student_id: str, student: StudentUpdate):
    if not ObjectId.is_valid(student_id):
        raise HTTPException(status_code=400, detail="Invalid student ID")
    
    update_data = {k: v for k, v in student.dict().items() if v is not None}
    if not update_data:
        raise HTTPException(status_code=400, detail="No data to update")
    
    result = await db.students.update_one(
        {"_id": ObjectId(student_id)},
        {"$set": update_data}
    )
    
    if result.modified_count == 0:
        raise HTTPException(status_code=404, detail="Student not found")
    
    updated_student = await db.students.find_one({"_id": ObjectId(student_id)})
    return student_helper(updated_student)

# 5. Delete Student
@app.delete("/api/students/{student_id}")
async def delete_student(student_id: str):
    if not ObjectId.is_valid(student_id):
        raise HTTPException(status_code=400, detail="Invalid student ID")
    
    result = await db.students.delete_one({"_id": ObjectId(student_id)})
    if result.deleted_count == 0:
        raise HTTPException(status_code=404, detail="Student not found")
    
    return {"message": "Student deleted successfully"}

# ==================== BOOK CRUD OPERATIONS ====================

# 6. Create Book
@app.post("/api/books", response_model=dict)
async def create_book(book: Book):
    book_dict = book.dict()
    result = await db.books.insert_one(book_dict)
    new_book = await db.books.find_one({"_id": result.inserted_id})
    return book_helper(new_book)

# 7. Read All Books
@app.get("/api/books", response_model=List[dict])
async def get_all_books():
    books = []
    async for book in db.books.find():
        books.append(book_helper(book))
    return books

# 8. Read Single Book
@app.get("/api/books/{book_id}", response_model=dict)
async def get_book(book_id: str):
    if not ObjectId.is_valid(book_id):
        raise HTTPException(status_code=400, detail="Invalid book ID")
    book = await db.books.find_one({"_id": ObjectId(book_id)})
    if book:
        return book_helper(book)
    raise HTTPException(status_code=404, detail="Book not found")

# 9. Update Book
@app.put("/api/books/{book_id}", response_model=dict)
async def update_book(book_id: str, book: BookUpdate):
    if not ObjectId.is_valid(book_id):
        raise HTTPException(status_code=400, detail="Invalid book ID")
    
    update_data = {k: v for k, v in book.dict().items() if v is not None}
    if not update_data:
        raise HTTPException(status_code=400, detail="No data to update")
    
    result = await db.books.update_one(
        {"_id": ObjectId(book_id)},
        {"$set": update_data}
    )
    
    if result.modified_count == 0:
        raise HTTPException(status_code=404, detail="Book not found")
    
    updated_book = await db.books.find_one({"_id": ObjectId(book_id)})
    return book_helper(updated_book)

# 10. Delete Book
@app.delete("/api/books/{book_id}")
async def delete_book(book_id: str):
    if not ObjectId.is_valid(book_id):
        raise HTTPException(status_code=400, detail="Invalid book ID")
    
    result = await db.books.delete_one({"_id": ObjectId(book_id)})
    if result.deleted_count == 0:
        raise HTTPException(status_code=404, detail="Book not found")
    
    return {"message": "Book deleted successfully"}

if __name__ == "__main__":
    import uvicorn
    import webbrowser
    import threading
    
    def open_browser():
        """Open browser after a short delay to allow server to start"""
        import time
        time.sleep(2)
        webbrowser.open("http://localhost:8000")
    
    # Start browser in a separate thread
    threading.Thread(target=open_browser, daemon=True).start()
    
    # Start the server
    uvicorn.run(app, host="0.0.0.0", port=8000)
