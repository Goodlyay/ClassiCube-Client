package com.mojang.net;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import com.mojang.util.LogUtil;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;

public final class NetworkHandler {

    public volatile boolean connected;
    public SocketChannel channel;
    public ByteBuffer in = ByteBuffer.allocate(1048576);
    public ByteBuffer out = ByteBuffer.allocate(1048576);
    public NetworkManager netManager;
    protected int soTrafficClass = 0x04 | 0x08 | 0x010;
    private byte[] stringBytes = new byte[64];

    public NetworkHandler(String ip, int port, Minecraft minecraft) {
        try {
            channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(ip, port));
            channel.configureBlocking(false);

            System.currentTimeMillis();
            /*
             * sock = channel.socket(); sock.setTcpNoDelay(true);
             * sock.setTrafficClass(soTrafficClass); sock.setKeepAlive(false);
             * sock.setReuseAddress(false); sock.setSoTimeout(100);
             * sock.getInetAddress().toString();
             */

            connected = true;
            in.clear();
            out.clear();

        } catch (Exception ex) {
            LogUtil.logWarning("Error initializing network connection to " + ip + ":" + port, ex);
            minecraft.setCurrentScreen(new ErrorScreen("Failed to connect",
                    "You failed to connect to the server. It\'s probably down!"));
            minecraft.isConnecting = false;

            minecraft.networkManager = null;
            netManager.successful = false;
        }
    }

    public final void close() {
        try {
            if (out.position() > 0) {
                out.flip();
                channel.write(out);
                out.compact();
            }
        } catch (Exception e) {
        }

        connected = false;

        try {
            channel.close();
        } catch (Exception e) {
        }

        channel = null;
    }

    @SuppressWarnings("rawtypes")
    public Object readObject(Class obj) {
        if (!connected) {
            return null;
        } else {
            try {
                if (obj == Long.TYPE) {
                    return in.getLong();
                } else if (obj == Integer.TYPE) {
                    return in.getInt();
                } else if (obj == Short.TYPE) {
                    return in.getShort();
                } else if (obj == Byte.TYPE) {
                    return in.get();
                } else if (obj == Double.TYPE) {
                    return in.getDouble();
                } else if (obj == Float.TYPE) {
                    return in.getFloat();
                } else if (obj == String.class) {
                    in.get(stringBytes);
                    return new String(stringBytes, "UTF-8").trim();
                } else if (obj == byte[].class) {
                    byte[] theBytes = new byte[1024];
                    in.get(theBytes);
                    return theBytes;
                } else {
                    return null;
                }
            } catch (Exception e) {
                netManager.error(e);
                return null;
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public final void send(PacketType packetType, Object... obj) {
        if (connected) {
            out.put(packetType.opcode);

            for (int i = 0; i < obj.length; ++i) {
                Class packetClass = packetType.params[i];
                Object packetObject = obj[i];
                if (connected) {
                    try {
                        if (packetClass == Long.TYPE) {
                            out.putLong((Long) packetObject);
                        } else if (packetClass == Integer.TYPE) {
                            out.putInt((Integer) packetObject);
                        } else if (packetClass == Short.TYPE) {
                            out.putShort(((Number) packetObject).shortValue());
                        } else if (packetClass == Byte.TYPE) {
                            out.put(((Number) packetObject).byteValue());
                        } else if (packetClass == Double.TYPE) {
                            out.putDouble((Double) packetObject);
                        } else if (packetClass == Float.TYPE) {
                            out.putFloat((Float) packetObject);
                        } else {
                            byte[] bytesToSend;
                            if (packetClass != String.class) {
                                if (packetClass == byte[].class) {
                                    if ((bytesToSend = (byte[]) packetObject).length < 1024) {
                                        bytesToSend = Arrays.copyOf(bytesToSend, 1024);
                                    }
                                    out.put(bytesToSend);
                                }
                            } else {
                                bytesToSend = ((String) packetObject).getBytes("UTF-8");
                                Arrays.fill(stringBytes, (byte) 32);

                                int j;
                                for (j = 0; j < 64 && j < bytesToSend.length; ++j) {
                                    stringBytes[j] = bytesToSend[j];
                                }

                                for (j = bytesToSend.length; j < 64; ++j) {
                                    stringBytes[j] = 32;
                                }

                                out.put(stringBytes);
                            }
                        }
                    } catch (Exception e) {
                        netManager.error(e);
                    }
                }
            }

        }
    }
}
