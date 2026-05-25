import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { PublicRoute } from "./components/PublicRoute";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { HomePage } from "./pages/HomePage";
import { SearchPage } from "./pages/SearchPage";
import { MainBookcasePage } from "./pages/MainBookcasePage";
import { BookcasePage } from "./pages/BookcasePage";
import {BookPage} from "./pages/BookPage";


export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                
                <Route path="/" element={<Navigate to="/home" replace />} />

                <Route path="/login" element={
                    <PublicRoute>
                        <LoginPage />
                    </PublicRoute>
                } />

                <Route path="/register" element={
                    <PublicRoute>
                        <RegisterPage />
                    </PublicRoute>
                } />

                <Route path="/home" element={
                    <ProtectedRoute>
                        <HomePage />
                    </ProtectedRoute>
                } />
                
                <Route path="/search" element={
                    <ProtectedRoute>
                        <SearchPage />
                    </ProtectedRoute>
                } />
                
                <Route path="/mainbookcase" element={
                    <ProtectedRoute>
                        <MainBookcasePage />
                    </ProtectedRoute>
                } />

                <Route path="/bookcase" element={
                    <ProtectedRoute>
                        <BookcasePage />
                    </ProtectedRoute>
                } />

                <Route path="/bookcase/showbook/:entryId" element={
                    <ProtectedRoute>
                        <BookPage />
                    </ProtectedRoute>
                } />

                <Route path="*" element={<Navigate to="/home" replace />} />

            </Routes>
        </BrowserRouter>
    );
}