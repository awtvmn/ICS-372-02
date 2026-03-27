package edu.metrostate;

/**
 * OrderStatus enum - represents the states of an order.
 * INCOMING: order has been received but not yet started.
 * IN_PROGRESS: order is currently being fulfilled.
 * COMPLETED: order has been fully fulfilled.
 * CANCELED: order was canceled before or during fulfillment. Feature 1.
 */
public enum OrderStatus {
    INCOMING,
    IN_PROGRESS,
    COMPLETED,
    CANCELED
}
