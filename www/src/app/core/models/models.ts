export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  city : string; 
}

export interface LoginResponse {
  token: string;
  userId: number;
  name: string;
  email: string;
  profilePicture?: string;
  role: 'USER' | 'ADMIN';
}

export interface MeetingRoom {
  id: number;
  name: string;
  location: string;
  floor: number;
  capacity: number;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
}

export interface CreateRoomRequest {
  name: string;
  location: string;
  floor: number;
  capacity: number;
}

export interface UpdateRoomRequest {
  name?: string;
  location?: string;
  floor?: number;
  capacity?: number;
}

export interface Reservation {
  id: number;
  roomId: number;
  roomName: string;
  roomLocation: string;
  userId: number;
  userName: string;
  startTime: string;
  endTime: string;
  status: 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'EXPIRED';
  purpose: string;
  createdAt: string;
}

export interface CreateReservationRequest {
  roomId: number;
  startTime: string;
  endTime: string;
  purpose?: string;
}

export interface ApiError {
  status: number;
  error: string;
  message: string;
}

export interface UserDto {
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
  profilePicture?: string;
  createdAt: string;
}
