package l1j.server.server.clientpackets;

import l1j.server.server.GameClient;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1BoardInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1BoardPost;

public class C_BoardDelete extends ClientBasePacket {

	private static final String C_BOARD_DELETE = "[C] C_BoardDelete";


	public C_BoardDelete(byte decrypt[], GameClient client) {
		super(decrypt);
		int objId = readD();
		int topicId = readD();
		L1Object obj = L1World.getInstance().findObject(objId);
		L1BoardInstance board = (L1BoardInstance) obj;
		L1PcInstance pc = client.getActiveChar();
		if (pc == null || board == null || obj == null)
			return;

		L1BoardPost topic = L1BoardPost.findById(topicId);
		if (topic == null) {
			return;
		}
		String name = client.getActiveChar().getName();
		if (!name.equals(topic.getName())) {
			return;
		}
		if (!pc.isGm()) {
			pc.sendPackets(new S_SystemMessage("掲示板書き込みは削除できません。"));
			return;
		}
		if (board.getNpcId() == 4200015) {
			topic.serverInfo();
		} else if (board.getNpcId() == 4200020) {
			topic.operator1();
		} else if (board.getNpcId() == 4200021) {
			topic.operator2();
		} else if (board.getNpcId() == 4200022) {
			topic.operator3();
		} else if (board.getNpcId() == 500002) {
			topic.proposal();
		} else {
			topic.board_Free();
		}
	}

	@Override
	public String getType() {
		return C_BOARD_DELETE;
	}
}
