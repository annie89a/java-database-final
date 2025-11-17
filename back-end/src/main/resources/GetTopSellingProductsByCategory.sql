DELIMITER $$
CREATE PROCEDURE GetTopSellingProductsByCategory(IN target_month INT, IN target_year INT)
BEGIN
    SELECT 
        p.category, 
        p.name, 
        SUM(oi.quantity) AS total_quantity_sold,
        SUM(oi.price * oi.quantity) AS total_sales
    FROM 
        product p
    JOIN 
        order_item oi ON p.id = oi.product_id
    JOIN 
        order_details od ON oi.order_id = od.id
    WHERE 
        MONTH(od.date) = target_month  -- use the provided month
        AND YEAR(od.date) = target_year  -- use the provided year
    GROUP BY 
        p.category, p.name
    HAVING 
        SUM(oi.quantity) = (
            SELECT 
                MAX(total_quantity)
            FROM (
                SELECT 
                    SUM(oi2.quantity) AS total_quantity
                FROM 
                    order_item oi2
                JOIN 
                    order_details od2 ON oi2.order_id = od2.id
                JOIN 
                    product p2 ON oi2.product_id = p2.id
                WHERE 
                    MONTH(od2.date) = target_month  -- same month
                    AND YEAR(od2.date) = target_year  -- same year
                    AND p2.category = p.category  -- same category
                GROUP BY 
                    p2.name  -- group by product name to calculate the total for each product
            ) AS Subquery
        )
    ORDER BY 
        p.category;
END$$
DELIMITER ;