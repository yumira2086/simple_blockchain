package com.blockchain.core.controller;

import com.blockchain.base.BaseData;
import com.blockchain.common.App;
import com.blockchain.common.ResultGenerator;
import com.blockchain.core.net.Connecter;
import com.blockchain.core.net.client.ClientStarter;
import com.blockchain.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tio.core.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by: Yumira.
 * Created on: 2018/7/30-下午3:01.
 * Description:
 */
@RestController
@RequestMapping("/node")
public class NodeController {


    /**
     * 当前在线节点
     */
    @PostMapping("/current")
    public BaseData getPool() throws ApiException {
        List<Node> currentOnlineNodes = Connecter.getCurrentOnlineNodesContainsLocal();
        return ResultGenerator.genSuccessResult(currentOnlineNodes);
    }

    /**
     * 手动连接到某一节点
     */
    @PostMapping("/connect")
    public BaseData connectTo(@RequestParam String ip,@RequestParam int port) throws ApiException{
        Set<Node> nodeSet = new HashSet<>();
        nodeSet.add(new Node(ip, port));
        Connecter.bindToGroup(nodeSet);
        return ResultGenerator.genSuccessResult("正在尝试连接，连接详情请查看日志");
    }

    /**
     * 清空本地节点数据
     */
    @PostMapping("/clear")
    public BaseData clear() throws ApiException{
        Connecter.clearNodes();
        return ResultGenerator.genSuccessResult();
    }
}
