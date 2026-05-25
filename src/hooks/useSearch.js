import { useState } from "react";
import { searchBooksByTitle, searchBooksByAuthor, saveBook } from "../services/api";

export function useSearch() {
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    async function searchByTitle(title) {
        try {
            setLoading(true);
            setError(null);
            const data = await searchBooksByTitle(title);
            setResults(data);
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false);
        }
    }

    async function searchByAuthor(authorName) {
        try {
            setLoading(true);
            setError(null);
            const data = await searchBooksByAuthor(authorName);
            setResults(data);
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false);
        }
    }

    async function save(bookDTO) {
        try {
            const saved = await saveBook(bookDTO);
            return saved;
        } catch (e) {
            setError(e.message);
            throw e;
        }
    }

    return { results, loading, error, searchByTitle, searchByAuthor, save };
}