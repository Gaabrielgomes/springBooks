const BASE_URL = "http://localhost:8889";

const defaultHeaders = {
    "Content-Type": "application/json"
};

// credentials: "include" is mandatory to send the necessary cookies
const withCredentials = {
    headers: defaultHeaders,
    credentials: "include"
};

export async function login(username, password) {
    const response = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        ...withCredentials,
        body: JSON.stringify({ username, password })
    });

    if (!response.ok) throw new Error("Invalid username or password.");
    return response.json();
}

export async function logout() {
    const response = await fetch(`${BASE_URL}/auth/logout`, {
        method: "POST",
        ...withCredentials
    });

    if (!response.ok) throw new Error("Logout failed.");
    return response.json();
}

export async function register(name, password, birth, gender, selfDescription) {
    const response = await fetch(`${BASE_URL}/auth/register`, {
        method: "POST",
        ...withCredentials,
        body: JSON.stringify({ name, password, birth, gender, selfDescription })
    });

    if (!response.ok) throw new Error("Registration failed.");
    return response.text();
}

export async function searchBooksByTitle(title) {
    const response = await fetch(
        `${BASE_URL}/books/search/bytitle?title=${encodeURIComponent(title)}`,
        { ...withCredentials, method: "GET" }
    );

    if (!response.ok) throw new Error("Search failed.");
    return response.json();
}

export async function searchBooksByAuthor(authorName) {
    const response = await fetch(
        `${BASE_URL}/books/search/byauthor?authorName=${encodeURIComponent(authorName)}`,
        { ...withCredentials, method: "GET" }
    );

    if (!response.ok) throw new Error("Search failed.");
    return response.json();
}

export async function getAllBooks() {
    const response = await fetch(`${BASE_URL}/books`,
        { ...withCredentials, method: "GET" }
    );

    if (!response.ok) throw new Error("Failed to fetch books.");
    return response.json();
}

export async function saveBook(bookDTO) {
    const response = await fetch(`${BASE_URL}/books/savebook`, {
        method: "POST",
        ...withCredentials,
        body: JSON.stringify(bookDTO)
    });

    if (!response.ok) throw new Error("Failed to save book.");
    return response.json();
}

export async function deleteBook(bookId) {
    const response = await fetch(`${BASE_URL}/books/delete/${bookId}`, {
        method: "DELETE",
        ...withCredentials
    });

    if (!response.ok) throw new Error("Failed to delete book.");
    return response.text();
}

export async function getMyProfile() {
    const response = await fetch(`${BASE_URL}/user`,
        { ...withCredentials, method: "GET" }
    );

    if (!response.ok) throw new Error("Failed to fetch profile.");
    return response.json();
}

export async function getMyBookcase() {
    const response = await fetch(`${BASE_URL}/user/bookcase`,
        { ...withCredentials, method: "GET" }
    );

    if (!response.ok) throw new Error("Failed to fetch bookcase.");
    return response.json();
}

export async function addBookToBookcase(bookId) {
    const response = await fetch(`${BASE_URL}/user/bookcase/addbook/${bookId}`, {
        method: "POST",
        ...withCredentials
    });

    if (!response.ok) throw new Error("Failed to add book to bookcase.");
    return response.text();
}

export async function updateReadingStatus(entryId, status) {
    const response = await fetch(`${BASE_URL}/user/bookcase/updatestatus/${entryId}`,
        {
            method: "PATCH",
            ...withCredentials,
            body: JSON.stringify(status)
        }
    );

    if (!response.ok) throw new Error("Failed to update reading status.");
    return response.text();
}

export async function addReview(bookId, comment) {
    const response = await fetch(`${BASE_URL}/user/bookcase/addreview/${bookId}`,
        {
            method: "PATCH",
            ...withCredentials,
            body: JSON.stringify(comment)
        }
    );

    if (!response.ok) throw new Error("Failed to add review.");
    return response.text();
}

export async function removeBookFromBookcase(bookId) {
    const response = await fetch(`${BASE_URL}/user/bookcase/removebook/${bookId}`, {
        method: "DELETE",
        ...withCredentials
    });

    if (!response.ok) throw new Error("Failed to remove book from bookcase.");
    return response.text();
}