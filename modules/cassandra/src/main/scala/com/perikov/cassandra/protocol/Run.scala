package com.perikov.cassandra.protocol

import cats.effect.*

object Samples:
  val headerBytes = Array[Byte](
    0x05,              // version
    0x0,               // flags
    0x0, 0x0,          // stream
    0x5,               // opcode
    0x0, 0x0, 0x0, 0x0 // length
  )
  def headerByteBuffer() = java.nio.ByteBuffer.wrap(headerBytes)
end Samples

object RunCassandra extends IOApp.Simple:
  import com.comcast.ip4s.*
  import fs2.io.net.Network
  import fs2.Chunk
  import java.nio.ByteBuffer
  import Samples.*


  val serverAddr     = SocketAddress(host"beelink", port"9042")
  val client         = Network[IO].client(serverAddr)
  val buf            = headerByteBuffer()
  println((buf.position, buf.capacity(), buf.limit()))

  def run =
    client.use { socket =>
      socket.write(Chunk.byteBuffer(buf)) *> socket
        .read(9).map(_.map(_.toByteBuffer.getInt(5)))
        .timed
        .flatMap(IO.println)
    }
