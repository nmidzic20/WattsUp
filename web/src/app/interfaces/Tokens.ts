export interface JwtInfo {
    id: string;
    firstName: string;
    email: string;
    roleId: string;
    role: string;
    exp: number;
}

export interface Tokens {
    jwtInfo: JwtInfo | null;
    jwt: string;
    refreshToken: string;
    refreshTokenExpiresAt: string;
}