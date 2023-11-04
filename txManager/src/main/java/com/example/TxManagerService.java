package com.example;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * txManager 도입으로 모든 메서드가 동일하게 동작함
 */
public class TxManagerService {

    private final Repository repository;
    private final PlatformTransactionManager txManager;
    private Long memberId = 0L;

    public TxManagerService(DataSource dataSource, PlatformTransactionManager txManager) {
        this.repository= new TxManagerRepository(dataSource);
        this.txManager = txManager;
    }

    public Member saveMember(Member member) throws SQLException {
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        try {
            member.setId(memberId++);
            txManager.commit(status);
        } catch (Exception e) {
            txManager.rollback(status);
            throw new IllegalStateException(e);
        }
        return repository.save(member);
    }

    /**
     * 트랜잭션매니저에 등록하지 않았다면 트랜잭션 없이 커넥션을 생성하고 쿼리수행
     */
    public Member findMemberById(Long memberId) throws SQLException {
        return repository.findById(memberId);
    }

    /**
     * from멤버에서 momey만큼 돈을 빼고, to멤버에서 money만큼 돈을 추가한다.
     *
     * @param fromId from멤버 id
     * @param toId   to멤버 id
     * @param money
     * @throws SQLException
     */
    public void accountTransfer(Long fromId, Long toId, Long money) throws SQLException {
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Member fromMember = repository.findById(fromId);
            Member toMember = repository.findById(toId);
            fromMember.setMoney(fromMember.getMoney() - money);
            toMember.setMoney(toMember.getMoney() + money);
            repository.updateMoney(fromId, fromMember.getMoney());
            repository.updateMoney(toId, toMember.getMoney());
            txManager.commit(status);
//            throw new Exception("트랜잭션 테스트옹 Exception");
        } catch (Exception e) {
            txManager.rollback(status);
            throw new IllegalStateException(e);
        }

    }

    /**
     * 멤버 테이블을 전체 delete한다. 테스트용 메서드
     *
     * @throws SQLException
     */
    public void deleteAll() throws SQLException {
        repository.deleteAll();
    }
}
