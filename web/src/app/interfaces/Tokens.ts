export interface JwtInfo {
    id: string;
    firstName: string;
    email: string;
    roleId: string;
    role: string;
    exp: number;
}

export interface Tokens {
    jwt: JwtInfo | null;
    refreshToken: string;
    refreshTokenExpiresAt: string;
}