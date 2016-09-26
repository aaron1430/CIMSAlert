/**
 * 
 */
package com.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alert.entity.Invoice;

/**
 * 发票任务
 * 
 * @author zjn
 * @date 2016年9月9日
 */
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

	// 根据发票ID查询发票详情
	@Query("select i from Invoice i where invoice_id=:invoice_id")
	Invoice findById(@Param("invoice_id") Integer invoiceId);

}
