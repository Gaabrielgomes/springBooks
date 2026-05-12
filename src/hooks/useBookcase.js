import { useState, useEffect } from "react";
import { getMyBookcase, addBookToBookcase, removeBookFromBookcase } from "../services/api";

export function useBookcase() {
    const [bookcase, setBookcase] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchBookcase();
    }, []);

    async function fetchBookcase() {
        try {
            setLoading(true);
            const data = await getMyBookcase();
            setBookcase(data);
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false);
        }
    }

    async function addBook(bookId) {
        try {
            await addBookToBookcase(bookId);
            await fetchBookcase();
        } catch (e) {
            setError(e.message);
        }
    }

    async function removeBook(bookId) {
        try {
            await removeBookFromBookcase(bookId);
            await fetchBookcase();
        } catch (e) {
            setError(e.message);
        }
    }

    return { bookcase, loading, error, addBook, removeBook };
}