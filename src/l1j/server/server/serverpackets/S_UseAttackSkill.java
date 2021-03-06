/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.serverpackets;

import java.util.concurrent.atomic.AtomicInteger;

import l1j.server.server.ActionCodes;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;


// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_UseAttackSkill extends ServerBasePacket {

    private static final String S_USE_ATTACK_SKILL = "[S] S_UseAttackSkill";
    private static AtomicInteger _sequentialNumber = new AtomicInteger(0);

    private byte[] _byte = null;

// public S_UseAttackSkill(L1Character caster, L1Character target,
// int spellgfx, boolean motion) {
// Point pt = target.getLocation();
// buildPacket(caster, target.getId(), spellgfx, pt.getX(), pt.getY(),
// ActionCodes.ACTION_SkillAttack, 6, motion);
// }

// public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
// int x, int y) {
// buildPacket(cha, targetobj, spellgfx, x, y,
// ActionCodes.ACTION_SkillAttack, 6, true);
// }

    public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
                            int x, int y, int actionId) {
        buildPacket(cha, targetobj, spellgfx, x, y, actionId, 6, true);
    }

    public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
                            int x, int y, int actionId, boolean motion) {
        buildPacket(cha, targetobj, spellgfx, x, y, actionId, 0, motion);
    }

    public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
                            int x, int y, int actionId, int isHit) {
        buildPacket(cha, targetobj, spellgfx, x, y, actionId, isHit, true);
    }

    private void buildPacket(L1Character cha, int targetobj, int spellgfx,
                             int x, int y, int actionId, int isHit, boolean withCastMotion) {
        if (cha instanceof L1PcInstance) {
            // 影系変身中に攻撃魔法を使用すると、クライアントが落ちるため、暫定対応
            if (cha.hasSkillEffect(L1SkillId.SHAPE_CHANGE)
                    && actionId == ActionCodes.ACTION_SkillAttack) {
                int tempchargfx = cha.getTempCharGfx();
                if (tempchargfx == 5727 || tempchargfx == 5730) {
                    actionId = ActionCodes.ACTION_SkillBuff;
                } else if (tempchargfx == 5733 || tempchargfx == 5736) {
                    // 補助魔法モーションにすると、攻撃魔法のグラフィックと
                    // 対象へのダメージモーショ​​ンが発生しなくなるため
                    // 攻撃モーションで代用
                    actionId = ActionCodes.ACTION_Attack;
                }
            }
        }
        // ファイアー主人がデフォルトであれば、攻撃魔法のグラフィックが発生しないため、強制的に置換
        // どこか別で管理することが好き？
        if (cha.getTempCharGfx() == 4013) {
            actionId = ActionCodes.ACTION_Attack;
        }

        int newheading = calcheading(cha.getX(), cha.getY(), x, y);
        cha.setHeading(newheading);
        writeC(Opcodes.S_ATTACK);
        writeC(actionId);
        writeD(withCastMotion ? cha.getId() : 0);
        writeD(targetobj);
        writeC(isHit);
        writeC(0);
        writeC(newheading);
        writeD(_sequentialNumber.incrementAndGet()); // 番号が重ならないように送る
        writeH(spellgfx);
        writeC(6); // ターゲットジジョン：6、範囲及びターゲットジジョン：8、範囲：0
        writeH(cha.getX());
        writeH(cha.getY());
        writeH(x);
        writeH(y);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
        writeC(0);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        } else {
            int seq = _sequentialNumber.incrementAndGet();
            _byte[13] = (byte) (seq & 0xff);
            _byte[14] = (byte) (seq >> 8 & 0xff);
            _byte[15] = (byte) (seq >> 16 & 0xff);
            _byte[16] = (byte) (seq >> 24 & 0xff);
        }

        return _byte;
    }

    private static int calcheading(int myx, int myy, int tx, int ty) {
        int newheading = 0;
        if (tx > myx && ty > myy) {
            newheading = 3;
        }
        if (tx < myx && ty < myy) {
            newheading = 7;
        }
        if (tx > myx && ty == myy) {
            newheading = 2;
        }
        if (tx < myx && ty == myy) {
            newheading = 6;
        }
        if (tx == myx && ty < myy) {
            newheading = 0;
        }
        if (tx == myx && ty > myy) {
            newheading = 4;
        }
        if (tx < myx && ty > myy) {
            newheading = 5;
        }
        if (tx > myx && ty < myy) {
            newheading = 1;
        }
        return newheading;
    }

    @Override
    public String getType() {
        return S_USE_ATTACK_SKILL;
    }

}