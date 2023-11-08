package transactionAop.example;

import jdbc.example.Member;
import jdbc.example.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@RequiredArgsConstructor
public class TransactionAopService {

    private final Repository repository;
    private Long memberId = 0L;


    @Transactional
    public Member saveMember(Member member) throws SQLException {
        member.setId(memberId++);
//            throw new IllegalStateException(e);
        return repository.save(member);
    }

    @Transactional(readOnly = true)
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
    @Transactional(noRollbackFor = IllegalStateException.class)
    public void accountTransfer(Long fromId, Long toId, Long money) throws SQLException {
        Member fromMember = repository.findById(fromId);
        Member toMember = repository.findById(toId);
        fromMember.setMoney(fromMember.getMoney() - money);
        toMember.setMoney(toMember.getMoney() + money);
        repository.updateMoney(fromId, fromMember.getMoney());
        repository.updateMoney(toId, toMember.getMoney());
        //Unchecked Exception인 RuntimeException을 발생시키니, 롤백이 되는 모습
        //throw new RuntimeException("트랜잭션 테스트옹 Exception");

        //RuntimeException을 상속받는 IllegalStateException.
        //noRollbackFor옵션으로 인해 Exception은 발생하나, 롤백은 되지않음.
        throw new IllegalStateException("noRollbackFor 테스트용 Exception");

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
