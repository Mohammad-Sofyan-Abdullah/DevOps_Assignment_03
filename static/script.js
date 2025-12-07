// API Base URL - uses current host for compatibility
const API_URL = `${window.location.protocol}//${window.location.host}/api`;

// Current edit item
let currentEditId = null;
let currentEditType = null;

// Initialize the app
document.addEventListener('DOMContentLoaded', () => {
    loadStudents();
    loadBooks();
    setupFormHandlers();
});

// Tab switching
function switchTab(tabName) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Remove active from all buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    document.getElementById(`${tabName}-section`).classList.add('active');
    
    // Set active button
    event.target.classList.add('active');
}

// Setup form handlers
function setupFormHandlers() {
    // Student form
    document.getElementById('student-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        await createStudent();
    });
    
    // Book form
    document.getElementById('book-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        await createBook();
    });
}

// ==================== STUDENT OPERATIONS ====================

// Create Student (Operation 1)
async function createStudent() {
    const student = {
        name: document.getElementById('student-name').value,
        age: parseInt(document.getElementById('student-age').value),
        email: document.getElementById('student-email').value,
        course: document.getElementById('student-course').value,
        grade: document.getElementById('student-grade').value || null
    };
    
    try {
        const response = await fetch(`${API_URL}/students`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(student)
        });
        
        if (response.ok) {
            showNotification('Student added successfully!', 'success');
            document.getElementById('student-form').reset();
            loadStudents();
        } else {
            throw new Error('Failed to add student');
        }
    } catch (error) {
        showNotification('Error adding student: ' + error.message, 'error');
    }
}

// Read All Students (Operation 2)
async function loadStudents() {
    try {
        const response = await fetch(`${API_URL}/students`);
        const students = await response.json();
        
        const studentsList = document.getElementById('students-list');
        studentsList.innerHTML = '';
        
        if (students.length === 0) {
            studentsList.innerHTML = '<p style="text-align: center; color: #999;">No students found. Add one!</p>';
            return;
        }
        
        students.forEach(student => {
            const card = createStudentCard(student);
            studentsList.appendChild(card);
        });
    } catch (error) {
        showNotification('Error loading students: ' + error.message, 'error');
    }
}

// Read Single Student (Operation 3)
async function getStudent(id) {
    try {
        const response = await fetch(`${API_URL}/students/${id}`);
        if (response.ok) {
            return await response.json();
        }
        throw new Error('Student not found');
    } catch (error) {
        showNotification('Error getting student: ' + error.message, 'error');
        return null;
    }
}

// Update Student (Operation 4)
async function updateStudent(id, data) {
    try {
        const response = await fetch(`${API_URL}/students/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            showNotification('Student updated successfully!', 'success');
            loadStudents();
            closeEditModal();
        } else {
            throw new Error('Failed to update student');
        }
    } catch (error) {
        showNotification('Error updating student: ' + error.message, 'error');
    }
}

// Delete Student (Operation 5)
async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student?')) return;
    
    try {
        const response = await fetch(`${API_URL}/students/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showNotification('Student deleted successfully!', 'success');
            loadStudents();
        } else {
            throw new Error('Failed to delete student');
        }
    } catch (error) {
        showNotification('Error deleting student: ' + error.message, 'error');
    }
}

// ==================== BOOK OPERATIONS ====================

// Create Book (Operation 6)
async function createBook() {
    const book = {
        title: document.getElementById('book-title').value,
        author: document.getElementById('book-author').value,
        isbn: document.getElementById('book-isbn').value,
        year: parseInt(document.getElementById('book-year').value),
        available: document.getElementById('book-available').checked
    };
    
    try {
        const response = await fetch(`${API_URL}/books`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(book)
        });
        
        if (response.ok) {
            showNotification('Book added successfully!', 'success');
            document.getElementById('book-form').reset();
            document.getElementById('book-available').checked = true;
            loadBooks();
        } else {
            throw new Error('Failed to add book');
        }
    } catch (error) {
        showNotification('Error adding book: ' + error.message, 'error');
    }
}

// Read All Books (Operation 7)
async function loadBooks() {
    try {
        const response = await fetch(`${API_URL}/books`);
        const books = await response.json();
        
        const booksList = document.getElementById('books-list');
        booksList.innerHTML = '';
        
        if (books.length === 0) {
            booksList.innerHTML = '<p style="text-align: center; color: #999;">No books found. Add one!</p>';
            return;
        }
        
        books.forEach(book => {
            const card = createBookCard(book);
            booksList.appendChild(card);
        });
    } catch (error) {
        showNotification('Error loading books: ' + error.message, 'error');
    }
}

// Read Single Book (Operation 8)
async function getBook(id) {
    try {
        const response = await fetch(`${API_URL}/books/${id}`);
        if (response.ok) {
            return await response.json();
        }
        throw new Error('Book not found');
    } catch (error) {
        showNotification('Error getting book: ' + error.message, 'error');
        return null;
    }
}

// Update Book (Operation 9)
async function updateBook(id, data) {
    try {
        const response = await fetch(`${API_URL}/books/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        if (response.ok) {
            showNotification('Book updated successfully!', 'success');
            loadBooks();
            closeEditModal();
        } else {
            throw new Error('Failed to update book');
        }
    } catch (error) {
        showNotification('Error updating book: ' + error.message, 'error');
    }
}

// Delete Book (Operation 10)
async function deleteBook(id) {
    if (!confirm('Are you sure you want to delete this book?')) return;
    
    try {
        const response = await fetch(`${API_URL}/books/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showNotification('Book deleted successfully!', 'success');
            loadBooks();
        } else {
            throw new Error('Failed to delete book');
        }
    } catch (error) {
        showNotification('Error deleting book: ' + error.message, 'error');
    }
}

// ==================== UI HELPERS ====================

function createStudentCard(student) {
    const card = document.createElement('div');
    card.className = 'item-card';
    card.innerHTML = `
        <h3>👨‍🎓 ${student.name}</h3>
        <p><strong>Age:</strong> ${student.age}</p>
        <p><strong>Email:</strong> ${student.email}</p>
        <p><strong>Course:</strong> ${student.course}</p>
        <p><strong>Grade:</strong> ${student.grade || 'Not assigned'}</p>
        <div class="item-actions">
            <button class="btn btn-edit" onclick="openEditStudentModal('${student.id}')">✏️ Edit</button>
            <button class="btn btn-delete" onclick="deleteStudent('${student.id}')">🗑️ Delete</button>
        </div>
    `;
    return card;
}

function createBookCard(book) {
    const card = document.createElement('div');
    card.className = 'item-card';
    card.innerHTML = `
        <h3>📖 ${book.title}</h3>
        <p><strong>Author:</strong> ${book.author}</p>
        <p><strong>ISBN:</strong> ${book.isbn}</p>
        <p><strong>Year:</strong> ${book.year}</p>
        <p><strong>Status:</strong> ${book.available ? '✅ Available' : '❌ Not Available'}</p>
        <div class="item-actions">
            <button class="btn btn-edit" onclick="openEditBookModal('${book.id}')">✏️ Edit</button>
            <button class="btn btn-delete" onclick="deleteBook('${book.id}')">🗑️ Delete</button>
        </div>
    `;
    return card;
}

// ==================== EDIT MODAL ====================

async function openEditStudentModal(id) {
    const student = await getStudent(id);
    if (!student) return;
    
    currentEditId = id;
    currentEditType = 'student';
    
    const modalTitle = document.getElementById('modal-title');
    modalTitle.textContent = 'Edit Student';
    
    const modalBody = document.getElementById('modal-body');
    modalBody.innerHTML = `
        <form id="edit-form">
            <div class="form-group">
                <label>Name:</label>
                <input type="text" id="edit-name" value="${student.name}" required>
            </div>
            <div class="form-group">
                <label>Age:</label>
                <input type="number" id="edit-age" value="${student.age}" required min="1">
            </div>
            <div class="form-group">
                <label>Email:</label>
                <input type="email" id="edit-email" value="${student.email}" required>
            </div>
            <div class="form-group">
                <label>Course:</label>
                <input type="text" id="edit-course" value="${student.course}" required>
            </div>
            <div class="form-group">
                <label>Grade:</label>
                <input type="text" id="edit-grade" value="${student.grade || ''}">
            </div>
            <button type="submit" class="btn btn-primary">Save Changes</button>
            <button type="button" class="btn btn-secondary" onclick="closeEditModal()">Cancel</button>
        </form>
    `;
    
    document.getElementById('edit-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            name: document.getElementById('edit-name').value,
            age: parseInt(document.getElementById('edit-age').value),
            email: document.getElementById('edit-email').value,
            course: document.getElementById('edit-course').value,
            grade: document.getElementById('edit-grade').value || null
        };
        await updateStudent(id, data);
    });
    
    document.getElementById('edit-modal').style.display = 'block';
}

async function openEditBookModal(id) {
    const book = await getBook(id);
    if (!book) return;
    
    currentEditId = id;
    currentEditType = 'book';
    
    const modalTitle = document.getElementById('modal-title');
    modalTitle.textContent = 'Edit Book';
    
    const modalBody = document.getElementById('modal-body');
    modalBody.innerHTML = `
        <form id="edit-form">
            <div class="form-group">
                <label>Title:</label>
                <input type="text" id="edit-title" value="${book.title}" required>
            </div>
            <div class="form-group">
                <label>Author:</label>
                <input type="text" id="edit-author" value="${book.author}" required>
            </div>
            <div class="form-group">
                <label>ISBN:</label>
                <input type="text" id="edit-isbn" value="${book.isbn}" required>
            </div>
            <div class="form-group">
                <label>Year:</label>
                <input type="number" id="edit-year" value="${book.year}" required min="1000" max="2100">
            </div>
            <div class="form-group">
                <label>
                    <input type="checkbox" id="edit-available" ${book.available ? 'checked' : ''}>
                    Available
                </label>
            </div>
            <button type="submit" class="btn btn-primary">Save Changes</button>
            <button type="button" class="btn btn-secondary" onclick="closeEditModal()">Cancel</button>
        </form>
    `;
    
    document.getElementById('edit-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            title: document.getElementById('edit-title').value,
            author: document.getElementById('edit-author').value,
            isbn: document.getElementById('edit-isbn').value,
            year: parseInt(document.getElementById('edit-year').value),
            available: document.getElementById('edit-available').checked
        };
        await updateBook(id, data);
    });
    
    document.getElementById('edit-modal').style.display = 'block';
}

function closeEditModal() {
    document.getElementById('edit-modal').style.display = 'none';
    currentEditId = null;
    currentEditType = null;
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('edit-modal');
    if (event.target == modal) {
        closeEditModal();
    }
}

// ==================== NOTIFICATIONS ====================

function showNotification(message, type) {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type} show`;
    
    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}
