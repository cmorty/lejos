; Auto-generated. Do not edit!


(cl:in-package nxt_lejos_msgs-msg)


;//! \htmlinclude Decibels.msg.html

(cl:defclass <Decibels> (roslisp-msg-protocol:ros-message)
  ((decibels
    :reader decibels
    :initarg :decibels
    :type cl:fixnum
    :initform 0))
)

(cl:defclass Decibels (<Decibels>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <Decibels>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'Decibels)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name nxt_lejos_msgs-msg:<Decibels> is deprecated: use nxt_lejos_msgs-msg:Decibels instead.")))

(cl:ensure-generic-function 'decibels-val :lambda-list '(m))
(cl:defmethod decibels-val ((m <Decibels>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader nxt_lejos_msgs-msg:decibels-val is deprecated.  Use nxt_lejos_msgs-msg:decibels instead.")
  (decibels m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <Decibels>) ostream)
  "Serializes a message object of type '<Decibels>"
  (cl:let* ((signed (cl:slot-value msg 'decibels)) (unsigned (cl:if (cl:< signed 0) (cl:+ signed 65536) signed)))
    (cl:write-byte (cl:ldb (cl:byte 8 0) unsigned) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) unsigned) ostream)
    )
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <Decibels>) istream)
  "Deserializes a message object of type '<Decibels>"
    (cl:let ((unsigned 0))
      (cl:setf (cl:ldb (cl:byte 8 0) unsigned) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) unsigned) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'decibels) (cl:if (cl:< unsigned 32768) unsigned (cl:- unsigned 65536))))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<Decibels>)))
  "Returns string type for a message object of type '<Decibels>"
  "nxt_lejos_msgs/Decibels")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'Decibels)))
  "Returns string type for a message object of type 'Decibels"
  "nxt_lejos_msgs/Decibels")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<Decibels>)))
  "Returns md5sum for a message object of type '<Decibels>"
  "e5a276d158da7c57089cff67e2bdc1cb")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'Decibels)))
  "Returns md5sum for a message object of type 'Decibels"
  "e5a276d158da7c57089cff67e2bdc1cb")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<Decibels>)))
  "Returns full string definition for message of type '<Decibels>"
  (cl:format cl:nil "int16 decibels~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'Decibels)))
  "Returns full string definition for message of type 'Decibels"
  (cl:format cl:nil "int16 decibels~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <Decibels>))
  (cl:+ 0
     2
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <Decibels>))
  "Converts a ROS message object to a list"
  (cl:list 'Decibels
    (cl:cons ':decibels (decibels msg))
))
