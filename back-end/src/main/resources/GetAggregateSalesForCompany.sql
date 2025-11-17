DELIMITER $$
CREATE PROCEDURE GetAggregateSalesForCompany(IN year_param INT, IN month_param INT)
BEGIN
    SELECT 
        SUM(DISTINCT od.total_price) AS total_sales,
        MONTH(od.date) AS sale_month,
        YEAR(od.date) AS sale_year
    FROM 
        order_details od
    JOIN 
        order_item oi ON od.id = oi.order_id
    WHERE 
        YEAR(od.date) = year_param
        AND MONTH(od.date) = month_param
    GROUP BY 
        MONTH(od.date), YEAR(od.date);
END$$
DELIMITER ;