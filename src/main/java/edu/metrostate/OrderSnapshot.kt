package edu.metrostate

data class OrderSnapshot (
    val orderID : Int,
    val status : OrderStatus,
    val startedAt : Long,
    val completedAt : Long,
    val canceledAt : Long
){

        //captures and saves recent order status
        companion object{
            fun from(order : Order): OrderSnapshot{
                return OrderSnapshot(
                    order.getOrderID(),
                    order.getOrderStatus(),
                    order.getStartedAt(),
                    order.getCompletedAt(),
                    order.getCanceledAt()

                )
            }
        }

    //restores saved results back into the order
        fun restore(order: Order) {
            order.setOrderStatus(status)
            order.setStartedAt(startedAt)
            order.setCompletedAt(completedAt)
            order.setCanceledAt(canceledAt)
        }
}
