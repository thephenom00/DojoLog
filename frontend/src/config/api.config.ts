const API_CONFIG = {
    // Vite exposes only VITE_-prefixed values to client-side code.
    BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',

    HEADERS: {
        "Content-Type": "application/json",
        "Accept": "application/json"
    }
};

export default API_CONFIG;
