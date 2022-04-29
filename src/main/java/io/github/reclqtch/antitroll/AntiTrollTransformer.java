package io.github.reclqtch.antitroll;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class AntiTrollTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transName, byte[] transClass) {
        if ("net.minecraft.client.network.NetHandlerPlayClient".equals(transName)) {
            boolean success1 = false;
            boolean success2 = false;
            try {
                ClassNode cn = new ClassNode();
                ClassReader cr = new ClassReader(transClass);
                cr.accept(cn, ClassReader.EXPAND_FRAMES);

                for (MethodNode method : cn.methods) {
                    if ("func_147252_a".equals(mapMethodName(cn, method))) {
                        ListIterator<AbstractInsnNode> insns = method.instructions.iterator();
                        while (insns.hasNext()) {
                            AbstractInsnNode insn = insns.next();
                            if (insn.getOpcode() == Opcodes.ICONST_5) {
                                InsnList list = new InsnList();
                                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/reclqtch/antitroll/Hooks", "onDemo", "()V", false));
                                list.add(new InsnNode(Opcodes.RETURN));
                                method.instructions.insert(insn.getNext(), list);
                                success1 = true;
                                break;
                            }
                        }
                    } else if ("func_147283_a".equals(mapMethodName(cn, method))) {
                        ListIterator<AbstractInsnNode> insns = method.instructions.iterator();
                        while (insns.hasNext()) {
                            AbstractInsnNode insn = insns.next();
                            if (insn.getOpcode() == Opcodes.NEW) {
                                InsnList list = new InsnList();
                                LabelNode label = new LabelNode();
                                list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/reclqtch/antitroll/Hooks", "shouldBlock", "(Lnet/minecraft/network/play/server/S27PacketExplosion;)Z", false));
                                list.add(new JumpInsnNode(Opcodes.IFEQ, label));
                                list.add(new InsnNode(Opcodes.RETURN));
                                list.add(label);
                                method.instructions.insertBefore(insn, list);
                                success2 = true;
                                break;
                            }
                        }
                    }
                }

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                return cw.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("AntiTroll > " + (success1 && success2 ? "Succe" : "Fail") + "ed Transforming " + transName);
            }
        }
        return transClass;
    }

    private static String mapMethodName(ClassNode classNode, MethodNode methodNode) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, methodNode.name, methodNode.desc);
    }
}
