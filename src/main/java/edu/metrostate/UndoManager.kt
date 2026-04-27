package edu.metrostate
import java.util.Stack;


class UndoManager {
    //saves previous order states
    private val history = Stack<OrderSnapshot>()

    //saves current state before making a change
    fun saveState(order : Order){
        history.push(OrderSnapshot.from(order));
    }

    //undo last action
    fun undo(orderManager : OrderManager): Boolean {
        if(history.isEmpty()) return false
        val snapshot = history.pop()
        val order = orderManager.getAllOrders()[snapshot.orderID]
            ?: return false
        snapshot.restore(order)

        return true
    }
}