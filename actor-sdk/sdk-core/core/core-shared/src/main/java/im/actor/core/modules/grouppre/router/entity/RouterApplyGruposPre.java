package im.actor.core.modules.grouppre.router.entity;

import java.util.List;

import im.actor.core.entity.GrupoPre;
import im.actor.core.modules.messaging.router.entity.RouterMessageOnlyActive;
import im.actor.runtime.actors.ask.AskMessage;
import im.actor.runtime.actors.messages.Void;


/**
 * Created by diego on 25/10/17.
 */

public class RouterApplyGruposPre implements AskMessage<Void>, RouterMessageOnlyActive {

    private Integer idGrupoPai;
    private List<GrupoPre> gruposPre;

    public RouterApplyGruposPre(Integer idGrupoPai, List<GrupoPre> gruposPre) {
        this.idGrupoPai = idGrupoPai;
        this.gruposPre = gruposPre;
    }

    public Integer getIdGrupoPai() {
        return idGrupoPai;
    }

    public List<GrupoPre> getGruposPre() {
        return gruposPre;
    }
}
