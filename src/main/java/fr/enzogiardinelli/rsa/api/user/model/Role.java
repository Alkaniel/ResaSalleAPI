package fr.enzogiardinelli.rsa.api.user.model;

public enum Role {
    SUPER_ADMIN, // ALL PERMS (MONITORING, CREATE MANAGER ACCOUNTS, DEBUG)
    MANAGER, // CAN ADD ROOMS, MODIFY ROOM CAPACITY, CANCEL BOOKINGS
    USER; // CAN BOOK A ROOM
}
